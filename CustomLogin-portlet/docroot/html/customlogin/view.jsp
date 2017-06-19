<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@page import="com.liferay.portal.model.Theme"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<portlet:defineObjects />

<portlet:actionURL var="loginForm" name="login"></portlet:actionURL>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  
  <portlet:resourceURL var="loginCred" id="login"></portlet:resourceURL>
  <portlet:resourceURL var="signUpCred" id="signUp"></portlet:resourceURL>
  


<script>
$(function(){
	
	$('#modal').click(function(e) {
		 $('#myModal').modal('show');
	})
	$('#loginButton').click(function(e) {
		 loginProcess();
	})
	
	
});

function loginProcess(){	
	var formData =  $('#loginForm').serialize();
	console.log(formData);
	 $.ajax({
		url : '${loginCred}',
		cache: false,
		data : formData,
		dataType : 'json',
		success : function(result) {
			console.log(result.response)
			if(result.response == "error"){
				alert("wrong password or username");
				console.log("errorrrrrrrrrrrrrrrrr");
			}else{
				location.href=result.response;
			}
		}
	 });
	
}


</script>

 <button type="button" class="btn btn-info btn-lg" id="modal">Open Modal</button>
 
   <div class="card card-container modal fade" role="dialog" id="myModal">
   <div style="margin: 0px;padding: 0px;" id="loginModal">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Login</h4>
      </div>
            <img id="profile-img" class="profile-img-card" src="//ssl.gstatic.com/accounts/ui/avatar_2x.png" />
            <p id="profile-name" class="profile-name-card"></p>
            <form class="form-signin" id="loginForm">
                <span id="reauth-email" class="reauth-email"></span>
                
                <input type="email" id="inputEmail" name="email" class="form-control" placeholder="Email address" required autofocus>
                
                <input type="password" id="inputPassword" name="pass" class="form-control" placeholder="Password" required>
                
                
                <div id="remember" class="checkbox">
                    <label>
                        <input type="checkbox" value="remember-me"> Remember me
                    </label>
                </div>
                
               
                
                <input type="button" id="loginButton" class="btn btn-lg btn-primary btn-block btn-signin" value="login"/>
                
            </form><!-- /form -->
            <a href="#" class="forgot-password">
                Forgot the password?
            </a>
          </div>
          
           </div><!-- /card-container -->
   
  
    
</body>
</html>
