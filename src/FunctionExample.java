public class FunctionExample {

  final String mapExample =
      "public static Map map(BufferedReader bufferedReader) throws IOException {\n"
          + "    Pattern pattern = Pattern.compile(\"[a-zA-Z]+\");\n"
          + "    Map<String, ArrayList<String>> map = new HashMap<>();\n"
          + "    Matcher matcher;\n"
          + "    String str = bufferedReader.readLine();\n"
          + "    while (str != null) {\n"
          + "      if (!str.equals(\"\")) {\n"
          + "        matcher = pattern.matcher(str);\n"
          + "        while (matcher.find()) {\n"
          + "          String word = matcher.group();\n"
          + "          if (!map.containsKey(word)) {\n"
          + "            map.put(word, new ArrayList<>());\n"
          + "            map.get(word).add(1 + \"\");\n"
          + "          } else\n"
          + "            map.get(word).add(1 + \"\");\n"
          + "        }\n"
          + "      }\n"
          + "      str = bufferedReader.readLine();\n"
          + "    }\n"
          + "    return map;\n"
          + "\n"
          + "  }";

  final String reduceExample = "public static Map reduce(Map<String,ArrayList<String>> map) {\n"
      + "\n"
      + "    Map<String, String> result = new HashMap<>();\n"
      + "\n"
      + "    for (Map.Entry<String,ArrayList<String>> entry : map.entrySet()) {\n"
      + "      result.put(entry.getKey(),entry.getValue().size()+\"\");\n"
      + "    }\n"
      + "\n"
      + "    return result;\n"
      + "  }";

  public String getMapExample() {
    return mapExample;
  }

  public String getReduceExample() {
    return reduceExample;
  }
}
