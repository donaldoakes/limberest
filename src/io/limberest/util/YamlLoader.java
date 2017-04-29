package io.limberest.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

@SuppressWarnings("rawtypes")
public class YamlLoader {

    private Object top;
    public Object getTop() { return top; }

    public YamlLoader(File file) throws IOException {
        this(new String(Files.readAllBytes(Paths.get(file.getPath()))));
    }

    public YamlLoader(String yamlStr) throws IOException {
        Yaml yaml = new Yaml();
        top = yaml.load(yamlStr);
    }
    
    /**
     * Empty loader.
     */
    public YamlLoader() {
        top = new HashMap();
    }

    public Map getMap(String name) {
        return getMap(name, top, "");
    }
    
    public Map getMap(String name, Object source) {
        Object obj = name.isEmpty() ? source : ((Map)source).get(name);
        if (obj == null)
            return null;
        if (!(obj instanceof Map))
            throw new YAMLException("Object: " + name + " is not Map");
        return (Map) obj;
    }

    public Map getMap(String name, Object source, String path) {
        Object obj = name.isEmpty() ? source : ((Map)source).get(name);
        if (obj == null)
            return null;
        if (!(obj instanceof Map))
            throw new YAMLException("Object: " + path + "/" + name + " is not Map");
        return (Map) obj;
    }

    public Map getRequiredMap(String name) {
        return getRequiredMap(name, top, "");
    }

    public Map getRequiredMap(String name, Object source, String path) {
        Object obj = name.isEmpty() ? source : ((Map)source).get(name);
        if (obj == null)
            missingYaml(name, path);
        if (!(obj instanceof Map))
            throw new YAMLException("Object: " + path + "/" + name + " is not Map");
        return (Map) obj;
    }

    public List getList(String name) {
        return getList(name, getRequiredMap("", top, ""));
    }
    
    public List getList(String name, Map source) {
        Object obj = name.isEmpty() ? source : ((Map)source).get(name);
        if (obj == null)
            return null;
        if (!(obj instanceof List))
            throw new YAMLException("Object: " + name + " is not List");
        return (List) obj;
    }

    public List getList(String name, Map source, String path) {
        Object obj = name.isEmpty() ? source : ((Map)source).get(name);
        if (obj == null)
            return null;
        if (!(obj instanceof List))
            throw new YAMLException("Object: " + path + "/" + name + " is not List");
        return (List) obj;
    }
    
    public List<String> getStringList(String name, Map source) {
        List<String> stringList = null;
        List<?> list = getList(name, source);
        if (list != null) {
            stringList = new ArrayList<>();
            for (Object o : list)
                stringList.add(o.toString());
        }
        return stringList;        
    }

    public List getRequiredList(String name) {
        return getRequiredList(name, getRequiredMap("", top, ""), "");
    }
    
    public List getRequiredList(String name, Map source, String path) {
        Object obj = name.isEmpty() ? source : ((Map)source).get(name);
        if (obj == null)
            missingYaml(name, path);
        if (!(obj instanceof List))
            throw new YAMLException("Object: " + path + "/" + name + " is not List");
        return (List) obj;
    }

    public String get(String name) {
        return get(name, getRequiredMap("", top, ""));
    }
    
    public String get(String name, Map source) {
        Object val = source.get(name);
        if (val == null)
            return null;
        return val.toString();
    }

    public String getRequired(String name) {
        return getRequired(name, getRequiredMap("", top, ""), "");
    }

    public String getRequired(String name, Map source, String path) {
        String val = get(name, source);
        if (val == null)
            missingYaml(name, path);
        return val;
    }
    
    public int getInt(String name, Map source) {
        String str = get(name, source);
        if (str == null)
            return 0;
        else
            return Integer.parseInt(str);
    }

    public void missingYaml(String name, String path) {
        String msg = "Missing required element: ";
        if (!path.isEmpty())
            msg += path + "/";
        if (!name.isEmpty())
            msg += name;
        throw new YAMLException(msg);
    }

    public String toString() {
        return new Yaml(new Representer(), getDumperOptions()).dump(top);
    }

    protected DumperOptions getDumperOptions() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
        options.setPrettyFlow(true);
        options.setIndent(2);
        return options;
    }

}
