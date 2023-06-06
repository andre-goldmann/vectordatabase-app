from typing import Union

from fastapi import FastAPI
import torch
from langchain.text_splitter import RecursiveCharacterTextSplitter
from sentence_transformers import SentenceTransformer

app = FastAPI()

@app.get("/items/{item_id}")
def read_item(item_id: int, q: Union[str, None] = None):
    return {"item_id": item_id, "q": q}


@app.get("/translator/{modelname}/")
def translate(modelname: str, text: str):
    model = getModel(modelname)

    # create the query vector
    return model.encode(text).tolist()

@app.get("/splitter/{text}")
def splitText(text: str):
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