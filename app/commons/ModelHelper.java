package commons;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class ModelHelper extends BeanUtilsBean {

    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if (value == null) return;
        //existing id should not be overwritten when updating attributes
        if (name.equals("id")) return;
        super.copyProperty(dest, name, value);
    }

    /**
     * Be aware, that all setter in destination class must have void as return value.
     * So no fluent-setter allowed.
     */
    public static void updateAttributes(Object destination, Object source) {
        try {
            new ModelHelper().copyProperties(destination, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}