package com.test;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.AuthException;
import com.liferay.portal.security.auth.Authenticator;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 * Portlet implementation class LoginCustom
 */
@Controller
@RequestMapping(value="view")
public class CustomLogin extends MVCPortlet {
	
	Log LOG = LogFactoryUtil.getLog(CustomLogin.class) ;

	
	@RenderMapping
	public String mainPage(){
		return "view";
	}
	
	@ResourceMapping(value="login")
	public void login(ResourceRequest resourceRequest,ResourceResponse resourceResponse,Model model) throws IOException, PortalException, SystemException{
		
		String login = ParamUtil.getString(resourceRequest,"email");
		String password = ParamUtil.getString(resourceRequest,"pass");
		PortletConfig portletConfig = (PortletConfig)resourceRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		LiferayPortletConfig liferayPortletConfig = (LiferayPortletConfig) portletConfig;
		SessionMessages.add(resourceRequest, liferayPortletConfig.getPortletId() + SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		

		
		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		
		
		try {
			boolean isAuthenticated=false;
			
			long userId= -1;
			userId = getAuthenticatedUserId(themeDisplay.getRequest(), login, password);
			List<User> userList= getUsersByEmailAddress(login);
			
			if(userList!=null&&userList.size()>0&&userId>=0){
				
				for(User user:userList){
					if(userId==user.getUserId()){
						if(user.getPasswordReset()){
							user.setPasswordModifiedDate(new Date());
							user.setPasswordModified(true);
							user.setPasswordReset(false);
							UserLocalServiceUtil.updateUser(user);
						}
						isAuthenticated=true;
					}
				}
				
			}

			if(isAuthenticated){
				
				String rememberMe = ParamUtil.getString(resourceRequest, "rememberMe");	
				String redirecturl=PortalUtil.getPortalURL(themeDisplay)+themeDisplay.getLayout().getFriendlyURL();

				String redirect = PortalUtil.getPathMain() + "/portal/login?login="+ login + "&password=" + password + "&rememberMe="+ rememberMe+"&redirect="+redirecturl;
				
				jsonObject.put("response",redirect);
				
			}
			
		
		}catch (Exception e) {
			// TODO Auto-generated catch block
			jsonObject.put("response","error");
			System.out.println("Login Error"+e.toString());
			SessionErrors.add(resourceRequest, "authentication-failed");
		}
	     HttpServletResponse httpResponse = PortalUtil.getHttpServletResponse(resourceResponse);
         ServletResponseUtil.write(httpResponse,jsonObject.toString());
	}
	
	

	
	public  long getAuthenticatedUserId(HttpServletRequest request, String login, String password)
			throws PortalException, SystemException {

			long userId = GetterUtil.getLong(login);

			Company company = PortalUtil.getCompany(request);

			String requestURI = request.getRequestURI();

			String contextPath = PortalUtil.getPathContext();

			if (requestURI.startsWith(contextPath.concat("/api/liferay"))) {
				throw new AuthException();
			}
			else {
				Map<String, String[]> headerMap = new HashMap<String, String[]>();

				Enumeration<String> enu1 = request.getHeaderNames();

				while (enu1.hasMoreElements()) {
					String name = enu1.nextElement();

					Enumeration<String> enu2 = request.getHeaders(name);

					List<String> headers = new ArrayList<String>();

					while (enu2.hasMoreElements()) {
						String value = enu2.nextElement();

						headers.add(value);
					}

					headerMap.put(
						name, headers.toArray(new String[headers.size()]));
				}

				Map<String, String[]> parameterMap = request.getParameterMap();
				Map<String, Object> resultsMap = new HashMap<String, Object>();

				int authResult = Authenticator.FAILURE;

			
				authResult = UserLocalServiceUtil.authenticateByEmailAddress(
					company.getCompanyId(), login, password, headerMap,
					parameterMap, resultsMap);

				userId = MapUtil.getLong(resultsMap, "userId", userId);				
			

				if (authResult != Authenticator.SUCCESS) {
					throw new AuthException();
				}
			}

			return userId;
		}
	
       private List<User> getUsersByEmailAddress(String emailAddress) {
		
		try {
			DynamicQuery userQuery = DynamicQueryFactoryUtil.forClass(User.class,PortalClassLoaderUtil.getClassLoader());
			Criterion criterion = RestrictionsFactoryUtil.eq("emailAddress",emailAddress);
			userQuery.add(criterion);
			List<User> userList = UserLocalServiceUtil.dynamicQuery(userQuery);
			return userList;
	} catch (SystemException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
 

}
