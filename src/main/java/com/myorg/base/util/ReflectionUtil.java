package com.myorg.base.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Reflection utility class
 * 
 * @author vijai
 */
public class ReflectionUtil {

	public static final String NO_NULL = "obj must not be null";

	private ReflectionUtil() {

	}

	/**
	 * Copies the fields from source object to target object
	 * 
	 * @return T
	 * @param source
	 * @param target
	 * @author vijai
	 */

	public static <T> T copyProperties(final T source, final T target) {
		nestedCopyProperties(source, target, (String[]) null);
		return target;
	}

	/**
	 * Copies the fields from source object with nested object to target object
	 * 
	 * @return T
	 * @param source
	 * @param target
	 * @author vijai
	 */
	public static void nestedCopyProperties(final Object source, final Object target, String... ignoreProperties) {
		nestedCopyProperties(source, target, null, (String[]) ignoreProperties);
	}

	/**
	 * Copies the fields from source object type <code>List</code> to target object
	 * <code>List</code>
	 * 
	 * @return T
	 * @param source
	 * @param target
	 * @author vijai
	 */
	public static <T, K> List<K> copyBeanPropertiesList(List<? extends T> list, Class<?> clazz) {
		List<K> newList = new ArrayList<>();
		ListIterator<?> iterater = list.listIterator();
		while (iterater.hasNext()) {
			@SuppressWarnings("unchecked")
			K k = (K) createInstance(clazz);
			copyProperties(iterater.next(), k);
			newList.add(k);
		}
		return newList;
	}

	/**
	 * Copies the nested source object properties to target object properties
	 * 
	 * @param source
	 * @param target
	 * @param editable
	 * @param isLambda
	 * @param ignoreProperties
	 */
	public static void nestedCopyProperties(Object source, Object target, Class<?> editable,
			String... ignoreProperties) {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");
		Class<?> actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class [" + target.getClass().getName()
						+ "] not assignable to Editable class [" + editable.getName() + "]");
			}
			actualEditable = editable;
		}
		List<PropertyDescriptor> targetPds = Arrays.asList(BeanUtils.getPropertyDescriptors(actualEditable));
		List<String> ignoreList = ignoreProperties != null ? Arrays.asList(ignoreProperties) : null;
		List<PropertyDescriptor> targetFiltered = targetPds.parallelStream()
				.filter(descr -> descr.getWriteMethod() != null).collect(Collectors.toList());
		if (ignoreList != null && !ignoreList.isEmpty()) {
			targetFiltered = targetFiltered.parallelStream()
					.filter(predicate -> ignoreList.parallelStream().noneMatch(predicate.getName()::contains))
					.collect(Collectors.toList());
		}
		targetFiltered.forEach(targetPd -> {
			PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
			if (sourcePd != null) {
				Method readMethod = sourcePd.getReadMethod();
				Method writeMethod = targetPd.getWriteMethod();
				if (readMethod != null
						&& ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
					invokeMethod(readMethod, targetPd, source, target, ignoreProperties);
				}
			}
		});
	}

	/**
	 * Sets the values to target object
	 * 
	 * @param readMethod
	 * @param targetPd
	 * @param source
	 * @param target
	 * @param editable
	 * @param ignoreProperties
	 */
	private static void invokeMethod(Method readMethod, PropertyDescriptor targetPd, Object source, Object target,
			String... ignoreProperties) {
		Method writeMethod = targetPd.getWriteMethod();
		try {
			setAccessible(readMethod);
			Object value = readMethod.invoke(source);
			setAccessible(writeMethod);
			if (value != null && !BeanUtils.isSimpleProperty(value.getClass())) {
				nestedCopyProperties(value, target, ignoreProperties);
			}
			if (value != null && value instanceof List) {
				value = copyListToList((List<?>) value, targetPd, target, ignoreProperties);
			}
			writeMethod.invoke(target, value);
		} catch (Exception ex) {
			throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target",
					ex);
		}

	}

	/**
	 * Set method accessible
	 * 
	 * @param method
	 */
	private static void setAccessible(Method method) {
		if (!Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
			method.setAccessible(true);
		}
	}

	/**
	 * copies the source list object to target list object
	 * 
	 * @param writeMethod
	 * @param list
	 * @param targetPd
	 * @param target
	 * @param ignoreProperties
	 * @throws NoSuchFieldException
	 */
	private static Object copyListToList(List<?> list, PropertyDescriptor targetPd, Object target,
			String... ignoreProperties) {
		List<Object> targetList = new ArrayList<>();
		try {
			Field[] fieldArray = getAllFieldsInHierarchy(target.getClass());
			List<Field> fields = Arrays.asList(fieldArray);
			Field tagetField = fields.parallelStream().filter(field -> field.getName().equals(targetPd.getName()))
					.collect(StreamUtil.singletonCollector());
			tagetField.setAccessible(true);
			ParameterizedType integerListType = (ParameterizedType) tagetField.getGenericType();
			Class<?> listClass = (Class<?>) integerListType.getActualTypeArguments()[0];
			list.parallelStream().forEach(param -> addToList(listClass, param, targetList, ignoreProperties));
		} catch (IllegalArgumentException | SecurityException e) {

			handleException(e);
		}
		return targetList;
	}

	/**
	 * Adds the copied object to target list
	 * 
	 * @param listClass
	 * @param param
	 * @param targetList
	 * @param ignoreProperties
	 */
	private static void addToList(Class<?> listClass, Object param, List<Object> targetList,
			String... ignoreProperties) {
		Object obj;
		try {
			obj = listClass.newInstance();
			nestedCopyProperties(param, obj, ignoreProperties);
			targetList.add(obj);
		} catch (InstantiationException | IllegalAccessException e) {
			handleException(e);
		}
	}

	/**
	 * Return the value of the given filed name for the given input object
	 * 
	 */
	public static Object getReadMethodValue(Object object, String fieldName) {

		Class<? extends Object> clazz = object.getClass();
		Object retrunObj = null;
		String firsLetter = fieldName.substring(0, 1);
		String upperCaseLetter = firsLetter.toUpperCase();
		String methodNm = fieldName.replaceFirst(firsLetter, upperCaseLetter);
		Method method;
		try {
			method = clazz.getMethod("get" + methodNm);
			boolean access = method.isAccessible();
			method.setAccessible(true);
			Object[] obj = new Object[0];
			retrunObj = method.invoke(object, obj);
			method.setAccessible(access);
		} catch (Exception ex) {
			ReflectionUtils.handleReflectionException(ex);
		}

		return retrunObj;
	}

	/**
	 * Handle the given reflection exception. Should only be called if no checked
	 * exception is expected to be thrown by the target method.
	 * <p>
	 * Throws the underlying RuntimeException or Error in case of an
	 * InvocationTargetException with such a root cause. Throws an
	 * IllegalStateException with an appropriate message or
	 * UndeclaredThrowableException otherwise.
	 * 
	 * @param ex
	 *            the reflection exception to handle
	 */
	public static void handleException(Exception ex) {
		if (ex instanceof TypeMismatchException) {
			throw new IllegalStateException("Could match the type: " + ex.getMessage());
		}
		if (ex instanceof NotWritablePropertyException) {
			throw new IllegalStateException("Unable to write property" + ex.getMessage());
		}
		if (ex instanceof ClassNotFoundException) {
			throw new IllegalStateException("Unable find the class" + ex.getMessage());
		}
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	/**
	 * Writes the value to setter method of the target Object to
	 * 
	 * @param value
	 * @param target
	 */
	public static void setWrapperPropertyValue(List<?> dtolist, Object target) {

		Assert.notNull(target, NO_NULL);
		dtolist.forEach(dto -> {
			Assert.notNull(dto, NO_NULL);
			List<PropertyDescriptor> targetPds = Arrays.asList(BeanUtils.getPropertyDescriptors(target.getClass()));
			targetPds.forEach(targetPd -> {
				Method writeMethod = targetPd.getWriteMethod();
				if (writeMethod != null && writeMethod.getParameterTypes()[0].isInstance(dto)
						&& Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
					try {
						writeMethod.setAccessible(true);
						writeMethod.invoke(target, dto);
					} catch (Exception ex) {
						throw new FatalBeanException("Could not write  '" + targetPd.getName() + "' to warpper", ex);
					}
				}
			});
		});
	}

	/**
	 * Return the value of the given filed name for the given input object
	 * 
	 */
	public static Object getNestedReadMethodValue(Object object, String fieldName) {

		Object retrunObj = null;
		List<PropertyDescriptor> propertyDescriptors = Arrays
				.asList(BeanUtils.getPropertyDescriptors(object.getClass()));
		Method method;
		try {
			PropertyDescriptor propertyDescriptor = propertyDescriptors.parallelStream()
					.filter(predicate -> predicate.getName().equalsIgnoreCase(fieldName))
					.collect(StreamUtil.singletonCollector());
			method = propertyDescriptor.getReadMethod();
			if (method != null) {
				boolean access = method.isAccessible();
				method.setAccessible(true);
				Object[] obj = new Object[0];
				retrunObj = method.invoke(object, obj);
				method.setAccessible(access);
			}

		} catch (Exception ex) {
			ReflectionUtils.handleReflectionException(ex);
		}

		return retrunObj;
	}

	public static boolean isGetMethodExist(Object obj, String methodName) {
		Assert.notNull(obj, NO_NULL);
		List<PropertyDescriptor> targetPdslist = Arrays.asList(BeanUtils.getPropertyDescriptors(obj.getClass()));
		return targetPdslist.stream().map(mapper -> mapper.getReadMethod().getName()).collect(Collectors.toList())
				.stream().anyMatch(methodName::contains);

	}

	public static Type getReadMethodGenericReturnType(Object obj, String propertyName) {
		Assert.notNull(obj, NO_NULL);
		PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(obj.getClass(), propertyName);
		return pd.getReadMethod().getGenericReturnType();
	}

	public static Method getReadMethod(Object obj, String propertyName) {
		Assert.notNull(obj, NO_NULL);
		PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(obj.getClass(), propertyName);
		return pd.getReadMethod();
	}

	public static List<String> getDecleredFieldList(Object obj) {
		Assert.notNull(obj, NO_NULL);
		List<PropertyDescriptor> propertyDescriptors = Arrays.asList(BeanUtils.getPropertyDescriptors(obj.getClass()));
		return propertyDescriptors.stream().map(PropertyDescriptor::getName).collect(Collectors.toList());
	}

	/**
	 * Returns Filed array of a class and its super classes
	 * 
	 * @param Class
	 * @return Filed[]
	 */
	public static Field[] getAllFieldsInHierarchy(Class<?> objectClass) {
		Set<Field> allFields = new HashSet<>();
		Field[] declaredFields = objectClass.getDeclaredFields();
		Field[] fields = objectClass.getFields();
		if (objectClass.getSuperclass() != null) {
			Class<?> superClass = objectClass.getSuperclass();
			Field[] superClassMethods = getAllFieldsInHierarchy(superClass);
			allFields.addAll(Arrays.asList(superClassMethods));
		}
		allFields.addAll(Arrays.asList(declaredFields));
		allFields.addAll(Arrays.asList(fields));
		return allFields.toArray(new Field[allFields.size()]);
	}

	/**
	 * Returns method array of a class and its super classes
	 * 
	 * @param Class
	 * @return Method[]
	 */
	public static Method[] getAllMethodsInHierarchy(Class<?> objectClass) {
		Set<Method> allMethods = new HashSet<>();
		Method[] declaredMethods = objectClass.getDeclaredMethods();
		Method[] methods = objectClass.getMethods();
		if (objectClass.getSuperclass() != null) {
			Class<?> superClass = objectClass.getSuperclass();
			Method[] superClassMethods = getAllMethodsInHierarchy(superClass);
			allMethods.addAll(Arrays.asList(superClassMethods));
		}
		allMethods.addAll(Arrays.asList(declaredMethods));
		allMethods.addAll(Arrays.asList(methods));
		return allMethods.toArray(new Method[allMethods.size()]);
	}

	public static String getBeanName(String propertyPath) {
		int separatorIndex = (propertyPath.endsWith(">") ? propertyPath.lastIndexOf('.') : -1);
		return (separatorIndex != -1 ? propertyPath.substring(separatorIndex + 1, propertyPath.indexOf('>'))
				: propertyPath).toLowerCase();
	}

	public static boolean isList(String propertyPath) {
		int separatorIndex = (propertyPath.endsWith(">") ? propertyPath.indexOf('<') : -1);
		return (separatorIndex != -1 ? propertyPath.substring(0, separatorIndex) : propertyPath).contains("List");
	}

	/**
	 * Get the underlying class for a type, or null if the type is a variable type.
	 *
	 * @param type
	 *            the type
	 * @return the underlying class
	 */
	public static Class<?> getClass(Type type) {
		if (type instanceof Class) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Class<?> componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static Object createInstance(Class<?> clazz) {
		Object obj = null;
		try {
			obj = clazz.newInstance();
		} catch (Exception e) {
			handleException(e);
		}
		return obj;
	}

	public static Object createInstance(String clazzName) {
		Object obj = null;
		try {
			Class<?> clazz = Class.forName(clazzName);
			obj = clazz.newInstance();
		} catch (Exception e) {
			handleException(e);
		}
		return obj;
	}
}
