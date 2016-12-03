package test;

import Controller.Controller;
import mvc.ModelAndView;
import RequestMapping.RequestMapping;

@Controller
public class test {
  
	@RequestMapping("/hello")
	public ModelAndView  hello(ModelAndView mdv) {
		ModelAndView mav=mdv;
		// TODO Auto-generated constructor stub
		mav.setViewName("test");
		mav.addObject("name", mav.getRequest_Map("name"));
		mav.addObject("pas", mav.getRequest_Map("pas"));
		return mav;
	}
	@RequestMapping("/hello2")
	public ModelAndView  hello2(ModelAndView mdv) {
		ModelAndView mav =mdv;
		// TODO Auto-generated constructor stub
		mav.setViewName("test");
		return mav;
	}
	
}
