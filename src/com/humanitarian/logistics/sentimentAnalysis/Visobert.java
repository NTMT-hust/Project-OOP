package com.humanitarian.logistics.sentimentAnalysis;

import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.huggingface.tokenizers.Encoding;

public class Visobert {
	public static void main(String[] args) {
        // 1. Load the tokenizer.json file
        // This automatically detects if it's BPE, WordPiece, etc.
        HuggingFaceTokenizer tokenizer = null;
		try {
			tokenizer = HuggingFaceTokenizer.newInstance(Paths.get("/resources/visobert/tokenizer.json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        String input = "Hello world, this is a test!";

        // 2. Encode (Convert text to numbers)
        Encoding encoding = tokenizer.encode(input);

        // 3. Get the arrays for ONNX Runtime
        long[] inputIds = encoding.getIds();
        long[] attentionMask = encoding.getAttentionMask();

        System.out.println("Input IDs: " + Arrays.toString(inputIds));
        // Output: [101, 7592, 2088, 1010, 2023, 2003, 1037, 3231, 999, 102]
        System.out.println("Attention mask: " + Arrays.toString(attentionMask));
        
        // Now you can pass `inputIds` directly to your OnnxTensor!
    }
}
