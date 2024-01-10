import logging
import os

from flask import Flask, jsonify, request
from transformers import BlenderbotForConditionalGeneration, BlenderbotTokenizer

app = Flask(__name__)

# Define model name and path
model_name = "facebook/blenderbot-400M-distill"
model_dir = "./blenderbot-400M-distill"  # Change to the appropriate path

# Set up logging
logging.basicConfig(level=logging.DEBUG)


# Function to check for the existence of the model and download it if necessary
def load_or_download_model(m_name, m_dir):
    if not os.path.exists(m_dir):
        os.makedirs(m_dir)
        logging.debug(f"Downloading model {m_name} to {m_dir}...")
        m = BlenderbotForConditionalGeneration.from_pretrained(m_name)
        t = BlenderbotTokenizer.from_pretrained(m_name)
        m.save_pretrained(m_dir)
        t.save_pretrained(m_dir)
        logging.debug("Model downloaded and saved.")
    else:
        logging.debug(f"Loading model from {m_dir}...")
        m = BlenderbotForConditionalGeneration.from_pretrained(m_dir)
        t = BlenderbotTokenizer.from_pretrained(m_dir)
    return m, t


# Load or download the model at application startup
model, tokenizer = load_or_download_model(model_name, model_dir)


@app.route("/ask", methods=["POST"])
def ask():
    try:
        # Get the question from the request
        data = request.json
        question = data["question"]

        # Process the question through the model
        inputs = tokenizer(question, return_tensors="pt")
        reply_ids = model.generate(**inputs)
        answer = tokenizer.decode(reply_ids[0], skip_special_tokens=True)

        # Return the answer
        return jsonify({"answer": answer})
    except Exception as e:
        logging.error(str(e))
        return jsonify({"error": str(e)})


if __name__ == "__main__":
    app.run(debug=True)
