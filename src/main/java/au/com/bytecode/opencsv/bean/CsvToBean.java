package au.com.bytecode.opencsv.bean;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 Copyright 2007 Kyle Miller.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import au.com.bytecode.opencsv.CSVReader;

public class CsvToBean<T> {
    private Map<Class<?>, PropertyEditor> editorMap = null;

    public CsvToBean() {
    }

    public List<T> parse(final MappingStrategy<T> mapper, final Reader reader) {
        return parse(mapper, new CSVReader(reader));
    }

    public List<T> parse(final MappingStrategy<T> mapper, final CSVReader csv) {
        try {
            mapper.captureHeader(csv);
            String[] line;
            final List<T> list = new ArrayList<T>();
            while (null != (line = csv.readNext())) {
                final T obj = processLine(mapper, line);
                list.add(obj); // TODO: (Kyle) null check object
            }
            return list;
        } catch (final Exception e) {
            throw new RuntimeException("Error parsing CSV!", e);
        }
    }

    protected T processLine(final MappingStrategy<T> mapper, final String[] line) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
        final T bean = mapper.createBean();
        for (int col = 0; col < line.length; col++) {
            final PropertyDescriptor prop = mapper.findDescriptor(col);
            if (null != prop) {
                final String value = checkForTrim(line[col], prop);
                final Object obj = convertValue(value, prop);
                prop.getWriteMethod().invoke(bean, obj);
            }
        }
        return bean;
    }

    private String checkForTrim(final String s, final PropertyDescriptor prop) {
        return trimmableProperty(prop) ? s.trim() : s;
    }

    private boolean trimmableProperty(final PropertyDescriptor prop) {
        return !prop.getPropertyType().getName().contains("String");
    }

    protected Object convertValue(final String value, final PropertyDescriptor prop) throws InstantiationException, IllegalAccessException {
        final PropertyEditor editor = getPropertyEditor(prop);
        Object obj = value;
        if (null != editor) {
            editor.setAsText(value);
            obj = editor.getValue();
        }
        return obj;
    }

    private PropertyEditor getPropertyEditorValue(final Class<?> cls) {
        if (editorMap == null) {
            editorMap = new HashMap<Class<?>, PropertyEditor>();
        }

        PropertyEditor editor = editorMap.get(cls);

        if (editor == null) {
            editor = PropertyEditorManager.findEditor(cls);
            addEditorToMap(cls, editor);
        }

        return editor;
    }

    private void addEditorToMap(final Class<?> cls, final PropertyEditor editor) {
        if (editor != null) {
            editorMap.put(cls, editor);
        }
    }


    /*
     * Attempt to find custom property editor on descriptor first, else try the propery editor manager.
     */
    protected PropertyEditor getPropertyEditor(final PropertyDescriptor desc) throws InstantiationException, IllegalAccessException {
        final Class<?> cls = desc.getPropertyEditorClass();
        if (null != cls) return (PropertyEditor) cls.newInstance();
        return getPropertyEditorValue(desc.getPropertyType());
    }

}
