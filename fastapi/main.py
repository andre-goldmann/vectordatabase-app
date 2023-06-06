import jwt
import torch
from fastapi import FastAPI
from fastapi import Header
from fastapi.middleware.cors import CORSMiddleware
from langchain.text_splitter import RecursiveCharacterTextSplitter
from sentence_transformers import SentenceTransformer

JWT_SECRET = "secret" # IRL we should NEVER hardcode the secret: it should be an evironment variable!!!
JWT_ALGORITHM = "HS256"

app = FastAPI()
origins = ["*"]
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/translator/{modelname}/")
def translate(modelname: str, text: str, authorization: str = Header(None)):

    try:
        print(authorization)
        decoded = secure(authorization)
        # here we can add code to check the user (by email)
        # e.g. select the user from the DB and see its permissions
    except:
        return "Unauthorized Access!"

    model = getModel(modelname)

    # create the query vector
    return model.encode(text).tolist()

@app.get("/splitter/{text}")
def splitText(text: str, authorization: str = Header(None)):

    try:
        print(authorization)
        decoded = secure(authorization)
        # here we can add code to check the user (by email)
        # e.g. select the user from the DB and see its permissions
    except:
        return "Unauthorized Access!"

    text_splitter = RecursiveCharacterTextSplitter(
        chunk_size=1536,
        chunk_overlap=200,
        # length_function=tiktoken_len,
        separators=["\n\n", "\n", " ", ""]
    )

    return text_splitter.split_text('\n'.join(text))


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

def secure(token):
    # if we want to sign/encrypt the JSON object: {"hello": "world"}, we can do it as follows
    # encoded = jwt.encode({"hello": "world"}, JWT_SECRET, algorithm=JWT_ALGORITHM)
    decoded_token = jwt.decode(token, JWT_SECRET, algorithms=JWT_ALGORITHM)
    # this is often used on the client side to encode the user's email address or other properties
    return decoded_token