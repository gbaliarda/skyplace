<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- <a href='<c:url value="/product/${param.productId}"/>' /> -->
<div class="border-jacarta-100 rounded-2.5xl h-32 relative flex items-center border bg-white p-4 transition-shadow hover:shadow-lg z-0">
    <a href="<c:url value="/product/${param.productId}"/>" class="flex inset-0 absolute w-full gap-2">
        <div class="flex flex-row items-center ml-4">
            <figure class="mr-5">
                <img src="<c:url value="/images/${param.nftImg}" />" class="w-[6rem] h-[6rem] rounded-lg aspect-square object-cover border border-gray-300" alt="avatar 2" loading="lazy">
            </figure>

            <div class="max-w-[34rem]">
                <h3 class="font-display text-jacarta-700 mb-1 text-base font-semibold truncate">
                    <c:out value="${param.nftName}" />#<c:out value="${param.nftCollectionId}"/>
                </h3>
                <div class="text-jacarta-500 block text-sm">
                    <c:choose>
                        <c:when test="${param.isMySale == true}">
                            <spring:message code="bidded.forSale" arguments="${[param.buyerUsername,param.price]}" />
                        </c:when>
                        <c:otherwise>
                            <spring:message code="bidded.bidded" arguments="${param.price}"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="text-sm pt-3">
                    <c:choose>
                        <c:when test="${param.isMySale == true}">
                            <spring:message code="buyoffer.pendingDate" arguments="${param.offerDate}"/>
                        </c:when>
                        <c:when test="${param.status == 'NEW'}">
                            <span><spring:message code="buyoffer.pending" /></span>
                        </c:when>
                        <c:otherwise>
                            <span><spring:message code="buyoffer.accepted" /></span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </a>
    <c:if test="${param.isAdmin == true || (param.isMySale == false && param.isOwner != null && param.isOwner == true)}">
        <c:choose>
            <c:when test="${param.status == 'NEW'}">
                <button type="submit" onclick="openDeleteOfferModal(${param.sellOrderId}, ${param.buyerId})" class="px-5 py-2 rounded-md text-white transition duration-300 shadow-md hover:shadow-xl bg-red-500 hover:bg-red-900 z-10 absolute right-8">
                    <spring:message code="buyoffer.delete" />
                </button>
            </c:when>
            <c:otherwise>
                <button onclick="openConfirmBuyOfferModal('${param.buyerUsername}', '${param.buyerWallet}', '${param.sellerUsername}', '${param.sellerWallet}', '${param.nftName}', '${param.nftContractAddr}', ${param.price}, ${param.nftCollectionId}, ${param.productId}, ${param.sellOrderId}, ${param.buyerId})" class="px-5 py-2 rounded-md text-white transition duration-300 shadow-md hover:shadow-xl bg-cyan-600 hover:bg-cyan-800 z-10 absolute right-8">
                    <spring:message code="buyoffer.confirm" />
                </button>
            </c:otherwise>
        </c:choose>
    </c:if>
</div>
