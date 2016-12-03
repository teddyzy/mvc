package mvc;

import java.io.File;
import java.io.IOExc;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletExc;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSerReq;
import javax.servlet.http.HttpSerRes;


public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	String java_root = "C:\\Users\\paukey01\\OneDrive\\javaee\\mvc\\src";
	ArrayList<Class<?>> ConClaList = new ArrayList<Class<?>>();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DispatcherServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpSerReq request, HttpSerRes response)
	 */
	protected void doGet(HttpSerReq request, HttpSerRes response) throws ServletExc, IOExc {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpSerReq request, HttpSerRes response)
	 */
	protected void doPost(HttpSerReq request, HttpSerRes response) throws ServletExc, IOExc {

		//寻找注解controller类
		LoadController(java_root);
	
		//匹配对应的RequestMapping，获取需要跳转页面的值和信息
		ModelView mdv=(ModelView) MatchRequestMapping(request.getServletPath(),getInput(request));
		
		for (Map.Entry<String, Object> entry : mdv.getObjectList().entrySet()) {  	
			request.setAttribute(entry.getKey(), entry.getValue());
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  	  
		}
			
		request.getRequestDispatcher(mdv.getViewName()+".jsp").forward(request, response);

	}
	
	
	/**
	 * 获得输入的所有属性和值并封装到ModelAndView中
	 * @param request
	 * @return ModelAndView
	 */
	public ModelView getInput(HttpSerReq request){		
		ModelView mav=new ModelView();
		//得到输入的所有属性和值
		Map<?, ?> map=request.getParameterMap();
		ArrayList<String> keyList = new ArrayList<String>();
		ArrayList<Object> valueList = new ArrayList<Object>();
		
		//遍历map		
		for (Object key : map.keySet()) { 
			keyList.add((String)key);	  
		}
		
		for (Object values : map.values()) {  
			String[]  value= (String[]) values;	
			valueList.add(value[0]);  
		}
		
		for(int i=0;i<keyList.size();i++){
			mav.addObject(keyList.get(i), valueList.get(i));
		}
		
		return mav;	
	}

	
	/**
	 * 遍历所有package下的含有Controller的java文件并保存到ConClaList
	 * @param filePath
	 */
	public void LoadController(String filePath) {
			File readFile = new File(filePath);
			File[] files = readFile.listFiles();
			
			String fileName = null;
			for (File file : files) {
				fileName = file.getName();
				if (file.isFile()) {
					
					//得到包下的java文件
					if (fileName.endsWith(".java")) {			
						try {							
							String  str=filePath+File.separator+ fileName;
							String beanClassName=str.substring(java_root.length()+1, str.length()-5).replace('\\', '.');
							Class<?> beanClass = Class.forName(beanClassName);
						
							//判断是否含有Controller注解,并加入到ConClaList
							if(beanClass.isAnnotationPresent(Controller.class)){
								ConClaList.add(beanClass);
							}										
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
					}
				} else {
					//遍历所有包下的java文件
					LoadController(filePath + File.separator + fileName);
				}
			}
	}

	/**
	 * 从所有控制类ConClaList中匹配对应的LoadRequestMapping,并返回方法的返回值
	 * @param servletPath
	 * @return 
	 */
	public Object MatchRequestMapping(String servletPath,ModelView mav){
		
		for(Class<?> ConClass: ConClaList ) {
			for(Method method : ConClass.getMethods()){      
	        	if(method.getAnnotation(RequestMapping.class).value().equals(servletPath)){
        		
	        		try {
	        			Object args[]=new Object[1];
	        			args[0]=mav;
						return method.invoke(ConClass.newInstance(),args);
					} catch (Exception e) {
						e.printStackTrace();
					} 
	        	}  
	  
	        }  
		}
		return null;	
	}
}
