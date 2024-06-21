from fastapi import FastAPI
from API.model import router as router1
from API.auth import router as router2
import uvicorn

app = FastAPI()

app.include_router(router1, prefix="/model")
app.include_router(router2, prefix="/auth")

@app.get("/")
async def read_root():
    return {"message": "Welcome to the ECOVISION"}

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=5000)