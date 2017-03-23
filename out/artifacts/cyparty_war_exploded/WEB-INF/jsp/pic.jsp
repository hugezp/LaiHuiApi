<%--
  Created by IntelliJ IDEA.
  User: zhu
  Date: 2017/1/11
  Time: 15:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
  <script src="http://manage.pinchenet.com/resource/js/jquery-1.11.3.min.js"></script>
</head>
<body>
<!-- 验证码 -->
<tr>
  <td nowrap width="437"></td>
    <img id="img" src="${ctx}/authImage?createTypeFlag=nl" />
    <a href='#' onclick="javascript:changeImg()" style="color:white;"><label style="color:black;">看不清？</label></a>
    <input type="text" value="${random}" id="input">
     <button type="button" onclick="checkFunction()"  height="40px" width="80px">提交</button>
  </td>
</tr>
<!-- 触发JS刷新-->
<script type="text/javascript">

  <%--var a = ${random};--%>
  function changeImg(){

    var img = document.getElementById("img");
    var val = document.getElementById("input");
    <%--${ctx}--%>
    img.src = "/authImage?date=" + new Date()+"&createTypeFlag=ch";

//    $("#input").val(a);
  }

  function checkFunction(){
    $.ajax({
      type: "POST",
      url: "/check/pic",
      data: {code:$('#input').val()},
      dataType: "json",
      success: function (data) {

      },
      error: function () {

      }
    })
  }
</script>
</body>
</html>
