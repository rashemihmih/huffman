import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Huffman {
    public void encode(String source, String destination) throws IOException {
        byte[] sourceBytes = Files.readAllBytes(Paths.get(source));
        Multiset<Byte> frequency = HashMultiset.create();
        frequency.add(null, 0);
        for (byte b : sourceBytes) {
            frequency.add(b);
        }
        Node tree = buildTree(frequency);
        Map<Byte, String> codes = calculateCodes(tree);
        for (Map.Entry<Byte, String> entry : codes.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
//        try (OutputStream os = new FileOutputStream(destination);
//             ObjectOutputStream oos = new ObjectOutputStream(os)) {
//            oos.writeObject(tree);
//        }
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
