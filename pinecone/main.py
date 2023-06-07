import json
import os
import sqlite3
import json
import os
import sqlite3
import traceback
from datetime import datetime
from json import JSONEncoder
from typing import List
from typing import Optional
from urllib.parse import urlparse

import docx
import pandas as pd
import pinecone
import pytz
import requests
import torch
import uvicorn
from anyio import create_task_group
from decouple import config
from fastapi import FastAPI, File, UploadFile, HTTPException, Form, Response, status, Depends
from fastapi.middleware.cors import CORSMiddleware
from langchain.document_loaders import PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from pinecone import GRPCIndex, DescribeIndexStatsResponse
from sentence_transformers import SentenceTransformer
from sklearn.ensemble import RandomForestClassifier
from tqdm.auto import tqdm
from typing_extensions import Annotated



class QueryResult:
    def __init__(self, id, score, text=''):
        self.id = id
        self.score = score
        self.text = text


class QueryResultEncoder(JSONEncoder):
    def default(self, o):
        return o.__dict__

def get_db():
    #con = sqlite3.connect(":memory:", check_same_thread=False)
    con = sqlite3.connect("files.db", check_same_thread=False)
    cur = con.cursor()
    cur.execute("CREATE TABLE IF NOT EXISTS files(name VARCHAR UNIQUE, ts timestamp)")
    try:
        yield con
    finally:
        con.close()

def existFile(filename:str, con):
    print(f"Search for file: {filename}")
    cur = con.cursor()
    res = cur.execute("SELECT count(*) FROM files WHERE name = ?", (filename,))
    result = res.fetchone()
    if result is None:
        return False

    return result[0] > 0

app = FastAPI()
origins = ["*"]
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.post("/pinecone/uploadfiles/url/")
async def create_upload_files_url(
        apiKey: str = Form(...),
        modelName: str = Form(...),
        indexName: str = Form(...),
        environment: str = Form(...),
        metric: str = Form(...),
        url: Optional[str] = Form(None),
        con=Depends(get_db)
):

    if "DEFAULT-API-KEY" == apiKey:
        apiKey = config('PINECONE_API_KEY')
    if "DEFAULT-MODEL-NAME" == modelName:
        modelName = "all-MiniLM-L6-v2"
    if "DEFAULT-INDEX-NAME" == indexName:
        indexName = "keyword-search"
    if "DEFAULT-ENVIRONMENT" == environment:
        environment = config('PINECONE_ENVIRONMENT')
    if "DEFAULT-METRIC" == metric:
        metric = "cosine"

    print(url)
    if url is not None and url != "":
        # TPD replace Link by url
        #link = "https://www.lcg.ufrj.br/nodejs/books/svelte-handbook.pdf"
        a = urlparse(url)
        fileName = os.path.basename(a.path)
        if existFile(fileName, con):
            print(f"file '{fileName}' allready exists'")
            return "OK"

        #print(a.path)                    # Output: /kyle/09-09-201315-47-571378756077.jpg
        #print(os.path.basename(a.path))  # Output: 09-09-201315-47-571378756077.jpg
        f = requests.get(url)
        upload_path = config('UPLOAD_PATH')
        open(f"{upload_path}{fileName}", 'wb').write(f.content)
        #file = open(f"{upload_path}{fileName}", "r")
        #print(f.text)
        file_location = f"{upload_path}{fileName}"
        await handleSingleFile(apiKey, con, environment, file_location, fileName, indexName, metric, modelName)

    return "OK"

#TODO extra Post für URL
@app.post("/pinecone/uploadfiles/")
async def create_upload_files(
        files: Annotated[
            List[UploadFile], File(description="Multiple files as UploadFile")
        ],
        apiKey: str = Form(...),
        modelName: str = Form(...),
        indexName: str = Form(...),
        environment: str = Form(...),
        metric: str = Form(...),
        con=Depends(get_db)
):

    if "DEFAULT-API-KEY" == apiKey:
        apiKey = config('PINECONE_API_KEY')
    if "DEFAULT-MODEL-NAME" == modelName:
        modelName = "all-MiniLM-L6-v2"
    if "DEFAULT-INDEX-NAME" == indexName:
        indexName = "keyword-search"
    if "DEFAULT-ENVIRONMENT" == environment:
        environment = config('PINECONE_ENVIRONMENT')
    if "DEFAULT-METRIC" == metric:
        metric = "cosine"

    for file in files:

        if existFile(file.filename, con):
            print(f"file '{file.filename}' allready exists'")
            continue

        upload_path = config('UPLOAD_PATH')
        fileName = file.filename
        file_location = f"{upload_path}{fileName}"
        print({"info": f"file '{fileName}' saving to '{file_location}'"})
        with open(file_location, "wb+") as file_object:
            file_object.write(file.file.read())

        await handleSingleFile(apiKey, con, environment, file_location, fileName, indexName, metric, modelName)

        #return {"filenames": [file.filename for file in files]}
        return "OK"


async def handleSingleFile(apiKey, con, environment, file_location, fileName, indexName, metric, modelName):
    try:

        # TODO start thread to store data into db otherwise it is taking to long

        # File needs to be stored first

        name, extension = os.path.splitext(file_location)
        print("parsing: " + file_location)
        if ".docx" == extension:
            docText = getText(file_location)
            useSplitter = False
            if useSplitter:
                text_splitter = RecursiveCharacterTextSplitter(
                    chunk_size=1536,
                    chunk_overlap=200,
                    # length_function=tiktoken_len,
                    separators=["\n\n", "\n", " ", ""]
                )

                split_text = text_splitter.split_text('\n'.join(docText))
                translateContent(modelName, indexName, apiKey, environment, metric, split_text)
            else:
                translateContent(modelName, indexName, apiKey, environment, metric, docText)
        elif ".pdf" == extension:
            loader = PyPDFLoader(file_location)
            pages = loader.load_and_split()

            if len(pages) > 0:

                text_splitter = RecursiveCharacterTextSplitter(
                    chunk_size=1536,
                    chunk_overlap=200,
                    # length_function=tiktoken_len,
                    # separators=["\n\n", "\n", " ", ""]
                    separators=["\n\n", "\n", " ", ""]
                )
                print("Pages within pdf: " + str(len(pages)))
                pageCounter = 0
                for page in pages:
                    pageContent = ''.join(page.page_content)
                    ##print("####################################################################")

                    split_text = text_splitter.split_text(pageContent)
                    # print("Seite: " + str(pageCounter) + ", Inhalt: " + str(len(pageContent)))
                    # print(pageContent)

                    if len(split_text) > 0:
                        translateContent(modelName, indexName, apiKey, environment, metric, split_text)
                    pageCounter = pageCounter + 1
        else:
            raise Exception("Unsupported Filetype: " + extension)

        cur = con.cursor()
        now = datetime.datetime.now()
        cur.execute("insert into files(name, ts) values (?, ?)", (fileName, now))
        con.commit()
        con.close()
        print(f"Stored file: {fileName}")
    except Exception as e:
        traceback.print_exc()
        print(e)
        raise HTTPException(status_code=500, detail='Something went wrong')
    #finally:
    #    file.file.close()


@app.get("/pinecone/fileinfo/{filename}")
async def fileInfo(filename: str, con=Depends(get_db)):
    upload_path = config('UPLOAD_PATH')
    #file_location = f"{upload_path}{filename}"
    #with open(file_location, "wb+") as file_object:
    #    file_object.read()
    #return os.path.isfile(file_location)
    #con = sqlite3.connect(":memory:")
    return existFile(filename, con)



@app.get("/pinecone/indexinfo/{apiKey}/{indexName}/{environment}")
async def pineconeInfo(
        apiKey: str,
        indexName: str,
        environment: str, response: Response):

    if "DEFAULT-API-KEY" == apiKey:
        apiKey = config('PINECONE_API_KEY')
    if "DEFAULT-INDEX-NAME" == indexName:
        indexName = "keyword-search"
    if "DEFAULT-ENVIRONMENT" == environment:
        environment = config('PINECONE_ENVIRONMENT')

    index: GRPCIndex = getIndexWithResponse(indexName, apiKey, environment, response)
    stats: DescribeIndexStatsResponse = index.describe_index_stats()

    return {"indexName": indexName,
            "dimension": stats.dimension,
            "index_fullness": stats.index_fullness,
            "total_vector_count": stats.total_vector_count}
    #return {"dimension": 0,
    #        "index_fullness": 0,
    #        "total_vector_count": 0}

@app.get("/pinecone/listmodels")
async def listModels():
    return ['all-MiniLM-L6-v2',
            'average_word_embeddings_komninos',
            'multi-qa-MiniLM-L6-cos-v1',
            'bert-base-nli-mean-tokens',
            'all_datasets_v3_mpnet-base',
            'paraphrase-MiniLM-L6-v2',
            'all-mpnet-base-v2',
            'average_word_embeddings_glove.6B.300d']

@app.get("/pinecone/listindexes")
async def listIndexes():
    pinecone.init(api_key=config('PINECONE_API_KEY'), environment=config('PINECONE_ENVIRONMENT'))
    return pinecone.list_indexes()

@app.get("/pinecone/fetchbyid")
def fetchById(indexName:str,
              apiKey: str,
              environment: str,
              searchid:str):

    if "DEFAULT-API-KEY" == apiKey:
        apiKey = config('PINECONE_API_KEY')
    if "DEFAULT-INDEX-NAME" == indexName:
        indexName = "keyword-search"
    if "DEFAULT-ENVIRONMENT" == environment:
        environment = config('PINECONE_ENVIRONMENT')

    index = getIndex(indexName, apiKey, environment)

    result = index.fetch([searchid])
    print(result)
    return str(result).replace("'", "\"")

@app.get("/pinecone/searchbyquery")
def searchByQuery(
        indexName:str,
        apiKey: str,
        environment: str,
        modelName: str,
        query: str):

    if indexName is None or indexName == "" or indexName == "undefined":
        print("indexName cannot be empty")
        return ""

    print(query)
    if "DEFAULT-API-KEY" == apiKey:
        apiKey = config('PINECONE_API_KEY')
    if "DEFAULT-MODEL-NAME" == modelName:
        modelName = "all-MiniLM-L6-v2"
    if "DEFAULT-INDEX-NAME" == indexName:
        indexName = "keyword-search"
    if "DEFAULT-ENVIRONMENT" == environment:
        environment = config('PINECONE_ENVIRONMENT')

    index = getIndex(indexName, apiKey, environment)

    model = getModel(modelName)

    query_questions = [
        query
    ]

    query_vectors = [model.encode(str(question)).tolist() for question in query_questions]

    # mit metadata###
    query_results = index.query(queries=query_vectors, top_k=5, include_metadata=True)
    #query_results = index.query(queries=query_vectors, top_k=5)

    #print(str(query_results).replace("'", "\""))
    scores = []
    for results in query_results['results']:
        for result in results['matches']:
            if 'text' in result['metadata']:
                print(f"{round(result['score'], 2)}: {result['metadata']['text']}")
                scores.append(
                    QueryResult(
                        result['id'],
                        round(result['score'], 2),
                        result['metadata']['text']
                    )
                )
            else:
                print(f"{round(result['score'], 2)}")
                #scores.pop()
                #.append(geeks('Akash', 2))
                scores.append(
                    QueryResult(
                        result['id'],
                        round(result['score'], 2)
                    )
                )

    #employeeJSONData = json.dumps(scores, indent=4, cls=QueryResultEncoder)
    #print(employeeJSONData)
    #return query_results
    #return str(query_results).replace("'", "\"")
    return json.dumps(scores, indent=4, cls=QueryResultEncoder)


def getText(filename):
    doc = docx.Document(filename)
    fullText = []
    for para in doc.paragraphs:
        fullText.append(para.text)
    return fullText

def getModel(modelname):
    if 'all-MiniLM-L6-v2' == modelname:
        device = 'cuda' if torch.cuda.is_available() else 'cpu'
        if device != 'cuda':
            print(f"You are using {device}. This is much slower than using "
                  "a CUDA-enabled GPU. If on Colab you can change this by "
                  "clicking Runtime > Change runtime type > GPU.")
        return SentenceTransformer(modelname, device=device)
    elif 'average_word_embeddings_komninos' == modelname:
        device = 'cuda' if torch.cuda.is_available() else 'cpu'
        return SentenceTransformer(modelname, device=device)
    elif 'multi-qa-MiniLM-L6-cos-v1' == modelname:
        device = 'cuda' if torch.cuda.is_available() else 'cpu'
        # load the retriever model from huggingface model hub
        return SentenceTransformer(modelname, device=device)
    elif 'bert-base-nli-mean-tokens' == modelname:
        return SentenceTransformer('sentence-transformers/bert-base-nli-mean-tokens')
    elif 'all_datasets_v3_mpnet-base' == modelname:
        return SentenceTransformer('flax-sentence-embeddings/all_datasets_v3_mpnet-base')
    elif 'paraphrase-MiniLM-L6-v2' == modelname:
        return SentenceTransformer(modelname)
    elif 'all-mpnet-base-v2' == modelname:
        return SentenceTransformer('sentence-transformers/all-mpnet-base-v2')
    elif 'average_word_embeddings_glove.6B.300d' == modelname:
        return SentenceTransformer(modelname)
    else:
        raise Exception("Unknown model: " + modelname)


def getAndCreateIndex(indexName:str, apiKey:str, environment:str, metric:str, model:SentenceTransformer):

    # for now always delete the index
    #if indexName in pinecone.list_indexes():
    #    pinecone.delete_index(indexName)

    pinecone.init(
        api_key=apiKey,  # find at app.pinecone.io
        environment=environment # next to api key in console
    )

    if indexName not in pinecone.list_indexes():
        pinecone.create_index(
            name=indexName,
            dimension=model.get_sentence_embedding_dimension(),
            metric=metric
        )

    # now connect to the index
    return pinecone.GRPCIndex(indexName)

def getIndex(indexName:str, apiKey:str, environment:str):
    print("####### GET-INDEX-FOR:")
    print(apiKey)
    print(indexName)
    print(environment)

    # now connect to the index
    try:
        pinecone.init(
            api_key=apiKey,  # find at app.pinecone.io
            environment=environment # next to api key in console
        )

        return pinecone.GRPCIndex(indexName)
    except Exception:
            raise HTTPException(
             status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
             detail='Error while connecting to index',
            )
        #response.status_code = status.HTTP_500_INTERNAL_SERVER_ERROR
        #response.status_code = status.HTTP_410_GONE

def getIndexWithResponse(indexName:str, apiKey:str, environment:str, response: Response):
    print("####### GET-INDEX-FOR:")
    print(apiKey)
    print(indexName)
    print(environment)

    # now connect to the index
    try:
        pinecone.init(
            api_key=apiKey,  # find at app.pinecone.io
            environment=environment # next to api key in console
        )

        return pinecone.GRPCIndex(indexName)
    except Exception:
        response.status_code = status.HTTP_410_GONE
        raise HTTPException(
            status_code=status.HTTP_410_GONE,
            detail='Error while connecting to index',
        )

    #response.status_code = status.HTTP_410_GONE

def translateContent(modelname: str, indexName:str, apiKey:str, environment:str, metric:str, content):

    model = getModel(modelname)
    #print("Model-Dim:" + str(model.get_sentence_embedding_dimension()))
    #embeddings = model.encode(content)
    index = getAndCreateIndex(indexName, apiKey, environment, metric, model)

    batch_size = 128

    for i in tqdm(range(0, len(content), batch_size)):
        # find end of batch
        i_end = min(i + batch_size, len(content))
        # create IDs batch
        ids = [str(x) for x in range(i, i_end)]
        # create metadata batch
        metadatas = [{'text': text} for text in content[i:i_end]]
        # create embeddings
        xc = model.encode(content[i:i_end])
        # create records list for upsert
        records = zip(ids, xc, metadatas)
        # upsert to Pinecone
        index.upsert(vectors=records)

    #print(embeddings)
    #return {"stats": index.describe_index_stats()}
    #return {"stats": 0}

#async def sometask(num):
#    print('Task', num, 'running')
#    await sleep(1)
#    print('Task', num, 'finished')

