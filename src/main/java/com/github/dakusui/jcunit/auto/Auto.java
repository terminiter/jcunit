package com.github.dakusui.jcunit.auto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.rules.TestName;

import com.github.dakusui.jcunit.core.Utils;
import com.github.dakusui.jcunit.exceptions.JCUnitException;
import com.github.dakusui.jcunit.exceptions.JCUnitRuntimeException;
import com.github.dakusui.lisj.Basic;
import com.github.dakusui.lisj.CUT;
import com.github.dakusui.lisj.Context;
import com.github.dakusui.lisj.FormResult;
import com.github.dakusui.lisj.func.BaseFunc;

public class Auto extends BaseFunc {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -2402014565260025741L;
	private String testName;

    /**
     * This function takes one and only one parameter, which is a name of a field.
     *  
     * @see com.github.dakusui.lisj.BaseForm#checkParams(java.lang.Object)
     */
    @Override
    protected Object checkParams(Object params) {
        super.checkParams(params);
        if (Basic.length(params) != 2) throw new IllegalArgumentException();
        Utils.checknull(Basic.get(params, 0));
        Utils.checknull(Basic.get(params, 1));
        return params;
    }

    @Override
    protected FormResult evaluateLast(
            Context context,
            Object[] evaluatedParams, 
            FormResult lastResult
            )
            throws JCUnitException, CUT {
        FormResult ret = lastResult;
        /*
         * We can use the first and second elements without a check since 'checkParams'
         * method guarantees that the array has two and only two elements. 
         */
        Object obj       = evaluatedParams[0].toString();
        String fieldName = evaluatedParams[1].toString();
        if (isAlreadyStored(obj, fieldName)) {
            store(obj, fieldName);
            ret.value(false);
        } else {
            Object previous = load(obj.getClass(), fieldName);
            Object current = get(obj, fieldName);
            ret.value(verify(previous, current, obj, fieldName));
        }
        return ret;
    }

    private File baseDir() {
        return new File(".jcunit");
    }
    
    private TestName testName() {
        return null;
    }
    
    public void testName(String testName) {
    	this.testName = testName;
    }

    private Field field(Class<?> clazz, String fieldName) throws JCUnitException {
        Field ret = null;
        try {
            ret = clazz.getField(fieldName);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    private File fileForField(File baseDir, TestName name, Field out) {
        return new File(baseDir, this.getClass().getCanonicalName() + "/" + name.getMethodName() + "/" + out.getName());
    }
    
    /**
     * Get a value of a field specified by <code>fieldName</code> from an object <code>obj</code>.
     * 
     * @param obj An object from which the returned value is retrieved.
     * @param fieldName An name of the field whose value is returned by this method.
     * @return Value of the field <code>fieldName</code> of <code>obj</code>
     */
    private Object get(Object obj, String fieldName) throws JCUnitException {
        Object ret = null;
        try {
            ret = field(obj.getClass(), fieldName).get(obj);
        } catch (IllegalArgumentException e) {
            ////
            // This clause shouldn't be executed since method 'field' should return
            // an appropriate field only and throw a runtime exception if there is no
            // appropriate one.
            assert false;
        } catch (IllegalAccessException e) {
            ////
            // This clause shouldn't be executed since method 'field' should return
            // an appropriate field only and throw a runtime exception if there is no
            // appropriate one.
            assert false;
        }
        return ret;
    }
    
    private Method equalsMethod(Object obj, String fieldName) {
        Method ret = null;
        try {
            ret = this.getClass().getDeclaredMethod("equals", Object.class, Object.class);
        } catch (SecurityException e) {
            assert false;
        } catch (NoSuchMethodException e) {
            assert false;
        }
        return ret;
    }

    private boolean verify(Object previous, Object current, Object obj, String fieldName) {
        Method equalsMethod = equalsMethod(obj, fieldName);
        Object verifiedResult = false;
        try {
            verifiedResult = equalsMethod.invoke(null, previous, current);
        } catch (IllegalArgumentException e) {
            assert false;
        } catch (IllegalAccessException e) {
            assert false;
        } catch (InvocationTargetException e) {
            assert false;
        }
        return ((Boolean)verifiedResult).booleanValue();
    }

    private Object load(Class<?> clazz, String fieldName) throws JCUnitException {
        return readObjectFromFile(fileForField(baseDir(), testName(), field(clazz, fieldName)));
    }

    private void store(Object obj, String fieldName) throws JCUnitException {
        Field field = field(obj.getClass(), fieldName);
        Object value;
        try {
            value = field.get(obj);
            saveObjectToFile(fileForField(baseDir(), testName(), field), value); 
        } catch (IllegalArgumentException e) {
            ////
            // This clause shouldn't be executed since method 'field' should return
            // an appropriate field only and throw a runtime exception if there is no
            // appropriate one.
            assert false;
        } catch (IllegalAccessException e) {
            ////
            // This clause shouldn't be executed since method 'field' should return
            // an appropriate field only and throw a runtime exception if there is no
            // appropriate one.
            assert false;
        }
    }

    private boolean isAlreadyStored(Object obj, String fieldName) throws JCUnitException {
        return fileForField(baseDir(), testName(), field(obj.getClass(), fieldName)).exists();
    }
    
    private Object readObjectFromFile(File f) {
        Object ret;
        
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
            try {
                ret = ois.readObject();
            } finally {
                ois.close();
            }
        } catch (ClassNotFoundException e) {
            String msg = String.format("Failed to deserialize an object in a file (%s)", f);
            throw new JCUnitRuntimeException(msg, e);
        } catch (FileNotFoundException e) {
            String msg = String.format("Failed to find a file (%s)", f);
            throw new JCUnitRuntimeException(msg, e);
        } catch (IOException e) {
            String msg = String.format("Failed to read object from a file (%s)", f);
            throw new JCUnitRuntimeException(msg, e);
        }
        return ret;
    }

    private void saveObjectToFile(File f, Object value) {
        try {
            if (!f.getParentFile().isDirectory() && !f.getParentFile().mkdirs()) {
                String msg = String.format("Failed to create a directory '%s'", f.getParentFile());
                throw new JCUnitRuntimeException(msg, null);
            }
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            try {
                oos.writeObject(value);
            } finally {
                oos.close();
            }
        } catch (FileNotFoundException e) {
            String msg = String.format("Failed to find a file (%s)", f);
            throw new JCUnitRuntimeException(msg, e);
        } catch (IOException e) {
            String msg = String.format("Failed to write object (%s) to a file (%s)", f, value);
            throw new JCUnitRuntimeException(msg, e);
        }
    }

    public static final boolean equals(Object expected, Object actual) {
        if (expected == null) {
            return actual == null;
        }
        return expected.equals(actual);
    }
}