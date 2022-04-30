<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<%@ include file="Head.jsp" %>
<body class="min-h-screen flex flex-col">
<%@ include file="../components/navbar.jsp" %>
    <div class="w-full max-w-5xl mx-auto p-4 grow flex flex-col justify-center py-12">
      <h1 class="text-3xl text-center">
        Sell an NFT
      </h1>
      <c:url value="/sell/${productId}" var="postPath"/>
      <form:form modelAttribute="sellNftForm" action="${postPath}" method="post" class="grid grid-cols-2 gap-8 pt-8" enctype="multipart/form-data">
        <div class="flex flex-col gap-1">
          <p class="text-slate-600">
            <span class="font-bold">NFT id: </span>
              ${nft.nft_id}
          </p>
        </div>
        <div class="flex flex-col gap-1">
          <p class="text-slate-600">
            <span class="font-bold">NFT contract: </span>
              ${nft.contract_addr}
          </p>
        </div>
        <form:label path="category" class="flex flex-col gap-1">
          <span class="text-slate-600">Category</span>
          <form:select
                  path="category"
                  required="true"
                  class="pl-3 sm:text-sm rounded-lg border-slate-300 focus:ring-cyan-800 focus:border-cyan-800 text-cyan-700 placeholder:text-slate-400 shadow-sm"
          >
            <c:forEach var="category" items="${categories}">
              <form:option value="${category}"><c:out value="${category}" /></form:option>
            </c:forEach>
          </form:select>
          <form:errors path="category" element="p" cssStyle="color: tomato" />
        </form:label>
        <form:label path="price" class="flex flex-col gap-1">
          <span class="text-slate-600 flex">Price<img class="w-8" src="<c:url value="/resources/eth_logo.svg" />" alt="ETH_logo"/> </span>
          <form:input
            type="number"
            path="price"
            autoComplete="off"
            required="true"
            placeholder="0.0"
            min="0"
            step="0.000000000000000001"
            class="pl-3 sm:text-sm rounded-lg border-slate-300 focus:ring-cyan-800 focus:border-cyan-800 text-cyan-700 placeholder:text-slate-400 shadow-sm"
          />
          <form:errors path="price" element="p" cssStyle="color: tomato" />
        </form:label>
        <input
          type="submit"
          value="Publish"
          class="px-1 py-4 col-start-2 font-bold rounded-lg shadow-sm cursor-pointer bg-cyan-100 text-cyan-700 hover:bg-cyan-200"
        />
        <form:errors path="publish" element="p" cssStyle="color: tomato" />
      </form:form>
    </div>
</body>
</html>
