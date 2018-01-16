import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Huffman {
    public void encode(String source, String destination) throws IOException {
        byte[] sourceBytes = Files.readAllBytes(Paths.get(source));
        Multiset<Byte> frequency = HashMultiset.create();
        for (byte b : sourceBytes) {
            frequency.add(b);
        }
        Node tree = buildTree(frequency);
        Map<Byte, String> codes = calculateCodes(tree);
        byte[] encoded = encodeBytes(sourceBytes, codes);
        try (OutputStream os = new FileOutputStream(destination);
             ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(tree);
            os.write(encoded);
        }
    }

    public void decode(String source, String destination) throws IOException {
        Node tree;
        List<Byte> sourceBytes = new ArrayList<>();
        try (InputStream is = new FileInputStream(source);
            ObjectInputStream ois = new ObjectInputStream(is)) {
            tree = (Node) ois.readObject();
            byte[] buf = new byte[4096];
            while (is.available() > 0) {
                int read = is.read(buf);
                for (int i = 0; i < read; i++) {
                    sourceBytes.add(buf[i]);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        List<Byte> decoded = decodeBytes(sourceBytes, tree);
        byte[] decodedArray = new byte[decoded.size()];
        for (int i = 0; i < decoded.size(); i++) {
            decodedArray[i] = decoded.get(i);
        }
        try (OutputStream os = new FileOutputStream(destination)) {
            os.write(decodedArray);
        }
    }

    private List<Byte> decodeBytes(List<Byte> source, Node tree) {
        StringBuilder sourceBinary = new StringBuilder();
        for (Byte b : source) {
            String byteBinary = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            sourceBinary.append(byteBinary);
        }
        List<Byte> decoded = new ArrayList<>();
        Node node = tree;
        for (int i = 0; i < sourceBinary.length(); i++) {
            char c = sourceBinary.charAt(i);
            if (c == '0') {
                node = node.getLeft();
            } else {
                node = node.getRight();
            }
            if (node.isLeaf()) {
                if (node.getValue() == null) {
                    return decoded;
                }
                decoded.add(node.getValue());
                node = tree;
            }
        }
        return decoded;
    }

    private byte[] encodeBytes(byte[] source, Map<Byte, String> codes) {
        StringBuilder encodedString = new StringBuilder();
        for (byte b : source) {
            String code = codes.get(b);
            encodedString.append(code);
        }
        if (encodedString.length() % 8 != 0) {
            encodedString.append(codes.get(null));
            while (encodedString.length() % 8 != 0) {
                encodedString.append('1');
            }
        }
        byte[] encodedBytes = new byte[encodedString.length() / 8];
        for (int i = 0; i < encodedString.length() / 8; i++) {
            String byteString = encodedString.substring(i * 8, i * 8 + 8);
            encodedBytes[i] = (byte) Integer.parseInt(byteString, 2);
        }
        return encodedBytes;
    }

    private Map<Byte, String> calculateCodes(Node tree) {
        Map<Byte, String> codes = new HashMap<>();
        calculateCodes(tree, new StringBuilder(), codes);
        return codes;
    }

    private void calculateCodes(Node node, StringBuilder code, Map<Byte, String> codes) {
        if (node.isLeaf()) {
            codes.put(node.getValue(), code.toString());
            return;
        }
        code.append('0');
        calculateCodes(node.getLeft(), code, codes);
        code.deleteCharAt(code.length() - 1);
        code.append('1');
        calculateCodes(node.getRight(), code, codes);
        code.deleteCharAt(code.length() - 1);
    }

    private Node buildTree(Multiset<Byte> frequency) {
        List<Node> nodes = frequency.entrySet().stream()
                .map(e -> new Node(e.getElement(), e.getCount()))
                .collect(Collectors.toList());
        nodes.add(new Node(null, 0));
        while (nodes.size() > 1) {
            nodes.sort(Comparator.comparingInt(Node::getWeight).reversed());
            Node right = nodes.get(nodes.size() - 1);
            nodes.remove(nodes.size() - 1);
            Node left = nodes.get(nodes.size() - 1);
            nodes.remove(nodes.size() - 1);
            Node node = new Node((byte) 0, left.getWeight() + right.getWeight(), left, right);
            nodes.add(node);
        }
        return nodes.get(0);
    }
}
