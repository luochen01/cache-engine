package com.ebay.cache;

public class ClasspathUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// 一般推荐用此方法
		// 获取当前ClassPath的绝对URI路径
		System.out.println("Thread.currentThread().getContextClassLoader():");
		System.out.println(Thread.currentThread().getContextClassLoader()
				.getResource(""));
		System.out.println("---------------------------------------");

		System.out.println("ClasspathUtil.class.getResource:");
		// 获取当前类文件的URI目录
		System.out.println(ClasspathUtil.class.getResource(""));

		// 获取当前的ClassPath的绝对URI路径。
		System.out.println(ClasspathUtil.class.getResource("/"));

		System.out.println("---------------------------------------");

		System.out.println("ClasspathUtil.class.getClassLoader().getResource:");

		// 获取当前ClassPath的绝对URI路径
		System.out
				.println(ClasspathUtil.class.getClassLoader().getResource(""));

		System.out.println("---------------------------------------");

		// 获取当前ClassPath的绝对URI路径
		System.out.println("ClassLoader.getSystemResource:");

		System.out.println(ClassLoader.getSystemResource(""));
		System.out.println("---------------------------------------");

		System.out.println("System.getProperty:");

		// 对于一般项目，这是项目的根路径。对于JavaEE服务器，这可能是服务器的某个路径。
		// 这个并没有统一的规范！所以，绝对不要使用“相对于当前用户目录的相对路径”。
		System.out.println(System.getProperty("user.dir"));
		System.out.println("---------------------------------------");

	}
}