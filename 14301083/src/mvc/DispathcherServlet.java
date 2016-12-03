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

		//Ѱ��ע��controller��
		LoadController(java_root);
	
		//ƥ���Ӧ��RequestMapping����ȡ��Ҫ��תҳ���ֵ����Ϣ
		ModelView mdv=(ModelView) MatchRequestMapping(request.getServletPath(),getInput(request));
		
		for (Map.Entry<String, Object> entry : mdv.getObjectList().entrySet()) {  	
			request.setAttribute(entry.getKey(), entry.getValue());
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  	  
		}
			
		request.getRequestDispatcher(mdv.getViewName()+".jsp").forward(request, response);

	}
	
	
	/**
	 * ���������������Ժ�ֵ����װ��ModelAndView��
	 * @param request
	 * @return ModelAndView
	 */
	public ModelView getInput(HttpSerReq request){		
		ModelView mav=new ModelView();
		//�õ�������������Ժ�ֵ
		Map<?, ?> map=request.getParameterMap();
		ArrayList<String> keyList = new ArrayList<String>();
		ArrayList<Object> valueList = new ArrayList<Object>();
		
		//����map		
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
	 * ��������package�µĺ���Controller��java�ļ������浽ConClaList
	 * @param filePath
	 */
	public void LoadController(String filePath) {
			File readFile = new File(filePath);
			File[] files = readFile.listFiles();
			
			String fileName = null;
			for (File file : files) {
				fileName = file.getName();
				if (file.isFile()) {
					
					//�õ����µ�java�ļ�
					if (fileName.endsWith(".java")) {			
						try {							
							String  str=filePath+File.separator+ fileName;
							String beanClassName=str.substring(java_root.length()+1, str.length()-5).replace('\\', '.');
							Class<?> beanClass = Class.forName(beanClassName);
						
							//�ж��Ƿ���Controllerע��,�����뵽ConClaList
							if(beanClass.isAnnotationPresent(Controller.class)){
								ConClaList.add(beanClass);
							}										
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
					}
				} else {
					//�������а��µ�java�ļ�
					LoadController(filePath + File.separator + fileName);
				}
			}
	}

	/**
	 * �����п�����ConClaList��ƥ���Ӧ��LoadRequestMapping,�����ط����ķ���ֵ
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
