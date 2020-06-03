/*
 * Copyright (c) 2017 Lixo Technologies Pvt Ltd
 */
package com.myorg.base.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 
 * @author vijai
 *
 */
public interface StreamUtil {
	/**
	 * Collector function which can used to get single object of list
	 * 
	 * @return
	 */
	public static <T, K> Collector<T, K, T> singletonCollector() {
		return getCollectorFunction(getListTSingleFunction());
	}

	/**
	 * Collector function which can used to get first object of list
	 * 
	 * @return
	 */
	public static <T, K> Collector<T, K, T> firstInListCollector() {
		return getCollectorFunction(getListTFirstFunction());
	}

	/**
	 * Collector function which can used to get last object of list
	 * 
	 * @return
	 */
	public static <T, K> Collector<T, K, T> lastInListCollector() {
		return getCollectorFunction(getLastTFirstFunction());
	}

	/**
	 * Methods return {@link Collectors} based on the given collect function
	 * 
	 * @param listTFunction
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T, K> Collector<T, K, T> getCollectorFunction(Function<List<T>, T> listTFunction) {
		return (Collector<T, K, T>) Collectors.collectingAndThen(Collectors.toList(), listTFunction);
	}

	/**
	 * Lambda function return single object of a list
	 * 
	 * @return
	 */
	static <T> Function<List<T>, T> getListTSingleFunction() {
		return list -> {
			if (list.size() != 1) {
				throw new IllegalStateException();
			}
			return list.get(0);
		};
	}

	/**
	 * Lambda function return First object of a list
	 * @return
	 */
	static <T> Function<List<T>, T> getListTFirstFunction() {
		return list -> {
			if (list.isEmpty()) {
				throw new IllegalStateException();
			}
			return list.get(0);
		};
	}
	/**
	 * Lambda function return last object of a list
	 * @return
	 */
	static <T> Function<List<T>, T> getLastTFirstFunction() {
		return list -> {
			if (list.isEmpty()) {
				throw new IllegalStateException();
			}
			return list.get(list.size() - 1);
		};
	}
}
