package com.humanitarian.logistics.userInterface.sentimentAnalysis;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Model implements AutoCloseable {

    private final ZooModel<String, Classifications> model;
    private final Predictor<String, Classifications> predictor;

    public Model(Path modelPath, Path tokenizerPath) throws Exception {
        // 1. Setup Tokenizer
        HuggingFaceTokenizer tokenizer = HuggingFaceTokenizer.builder()
                .optTokenizerPath(tokenizerPath)
                .build();

        // 2. Define Translator (Encapsulated logic)
        Translator<String, Classifications> translator = new Translator<String, Classifications>() {
            @Override
            public NDList processInput(TranslatorContext ctx, String input) {
                ai.djl.huggingface.tokenizers.Encoding encoding = tokenizer.encode(input);
                NDManager manager = ctx.getNDManager();

                NDArray inputIdArray = manager.create(encoding.getIds());
                inputIdArray.setName("input_ids");

                NDArray attentionMaskArray = manager.create(encoding.getAttentionMask());
                attentionMaskArray.setName("attention_mask");

                return new NDList(inputIdArray, attentionMaskArray);
            }

            @Override
            public Classifications processOutput(TranslatorContext ctx, NDList list) {
                NDArray logits = list.get(0);
                NDArray probabilities = logits.softmax(0); // The fix we made earlier
                List<String> classes = Arrays.asList("Negative", "Positive", "Neutral");
                return new Classifications(classes, probabilities);
            }

            @Override
            public Batchifier getBatchifier() {
                return Batchifier.STACK;
            }
        };

        // 3. Build Criteria
        Criteria<String, Classifications> criteria = Criteria.builder()
                .setTypes(String.class, Classifications.class)
                .optModelPath(modelPath)
                .optEngine("OnnxRuntime")
                .optTranslator(translator)
                .build();

        // 4. Load Model and create Predictor
        this.model = criteria.loadModel();
        this.predictor = model.newPredictor();
    }

    // A clean public method for other classes to use
    public Classifications predict(String text) throws Exception {
        return predictor.predict(text);
    }

    @Override
    public void close() {
        // Clean up resources when done
        if (predictor != null)
            predictor.close();
        if (model != null)
            model.close();
    }
}