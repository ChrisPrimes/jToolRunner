package com.chrisprimes.compilertest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

//https://stackoverflow.com/questions/2946338/how-do-i-programmatically-compile-and-instantiate-a-java-class
public class ToolCompiler 
{
	private String dynClassName = "ToolSource";
	
	private File getTempDirectory() {
		File systemTempDir = new File(System.getProperty("java.io.tmpdir"));
    	File applicationTempDir = new File(systemTempDir, ToolCompiler.class.getPackageName());
    	return applicationTempDir;
	}
	
	public byte[] compile(String source) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, CompilationException {
		File applicationTempDir = getTempDirectory();

    	File sourceFile = new File(applicationTempDir, dynClassName + ".java");
    	File compiledFile = new File(applicationTempDir, dynClassName + ".class");
    	sourceFile.getParentFile().mkdirs();
    	Files.write(sourceFile.toPath(), source.getBytes());

    	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    	
    	ByteArrayOutputStream compilationErrorStream = new ByteArrayOutputStream();
    	
    	int result = compiler.run(null, null, compilationErrorStream, sourceFile.getPath());
    	
    	if(result != 0) {
    		String compilationError = compilationErrorStream.toString();
    		throw new CompilationException(compilationError);
    	}

    	byte[] byteCode = Files.readAllBytes(compiledFile.toPath());
    	
    	sourceFile.delete();
    	compiledFile.delete();
    	
    	return byteCode;
	}
	
	public Tool getTool(byte[] bytecode) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		File applicationTempDir = getTempDirectory();
		File compiledFile = new File(applicationTempDir, dynClassName + ".class");
		
		Files.write(compiledFile.toPath(), bytecode);

    	URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { applicationTempDir.toURI().toURL() });
    	Class<?> cls = Class.forName(dynClassName, true, classLoader);
    	Tool instance = (Tool) cls.getConstructor().newInstance();
    	
    	compiledFile.delete();
    	
    	return instance;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, CompilationException {
		String source = "import com.chrisprimes.compilertest.Tool; public class ToolSource implements Tool { public ToolSource() { System.out.println(\"hello world\"); } public void print() { System.out.println(\"better hello world\"); } }";
		
		ToolCompiler tc = new ToolCompiler();
		byte[] code = tc.compile(source);
		tc.getTool(code).print();
	}
}