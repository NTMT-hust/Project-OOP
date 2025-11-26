package com.humanitarian.logistics.sentimentAnalysis;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Visobert {

    public static void main(String[] args) throws Exception {
        // 1. Setup paths
        Path modelPath = Paths.get("src/resources/visobert/model.onnx");
        Path tokenizerPath = Paths.get("src/resources/visobert/tokenizer.json");

        // 2. Load the Tokenizer (You already did this part)
        // We use HuggingFaceTokenizer via DJL
        HuggingFaceTokenizer tokenizer = HuggingFaceTokenizer.builder()
                .optTokenizerPath(tokenizerPath)
                .build();

        // 3. Define the Translator
        // This tells DJL how to feed the text into the ONNX model
        Translator<String, Classifications> translator = new Translator<String, Classifications>() {

        	@Override
        	public NDList processInput(TranslatorContext ctx, String input) {
        	    // 1. Tokenize
        	    Encoding encoding = tokenizer.encode(input);
        	    NDManager manager = ctx.getNDManager();

        	    // 2. Get data
        	    long[] inputIds = encoding.getIds();
        	    long[] attentionMask = encoding.getAttentionMask();

        	    // 3. Create NDArrays
        	    NDArray inputIdArray = manager.create(inputIds);
        	    NDArray attentionMaskArray = manager.create(attentionMask);

        	    // 4. Set Names (Must be done separately)
        	    inputIdArray.setName("input_ids");
        	    attentionMaskArray.setName("attention_mask");
        	    
        	    return new NDList(inputIdArray, attentionMaskArray);
        	}

        	@Override
        	public Classifications processOutput(TranslatorContext ctx, NDList list) {
        	    // 1. Get the raw output (logits) from the model
        	    ai.djl.ndarray.NDArray logits = list.get(0);
        	    
        	    // 2. Apply Softmax directly on the NDArray
        	    // We use axis 0 assuming the output is a simple 1D array of probabilities for this specific input
        	    ai.djl.ndarray.NDArray probabilities = logits.softmax(0);

        	    // 3. Create the Classifications object
        	    List<String> classes = Arrays.asList("Negative", "Positive"); // Make sure order matches your training!
        	    
        	    // Classifications requires a List<Double> or similar, so we use a helper to convert
        	    return new Classifications(classes, probabilities);
        	}
        };

        // 4. Build Criteria
        Criteria<String, Classifications> criteria = Criteria.builder()
                .setTypes(String.class, Classifications.class)
                .optModelPath(modelPath)
                .optEngine("OnnxRuntime") // Explicitly ask for ONNX
                .optTranslator(translator)
                .build();

        // 5. Run Inference
        try (ZooModel<String, Classifications> model = criteria.loadModel();
             Predictor<String, Classifications> predictor = model.newPredictor()) {
            
            String text = "tôi buồn vì bạn đánh tôi";
            Classifications result = predictor.predict(text);
            System.out.println("Sentiment: " + result);
        }
    }
}