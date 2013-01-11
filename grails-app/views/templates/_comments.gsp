<%@ page import="org.chai.memms.util.Utils" %>
<ul class="comment-list">
	<g:each in="${order.comments.sort{a,b -> (a.dateCreated < b.dateCreated) ? 1 : -1}}" status="i" var="comment">
		<li>
			<div class="delete-comment-option">
			  <span class="ajax-error"><g:message code="entity.error.updating.try.again"/></span>
			   <img src="${resource(dir:'images',file:'spinner.gif')}" class="ajax-spinner"/>
			    <a href="#" class="delete-comment delete-row" id="${comment.id}">X</a>
			</div>
			<div class="comment-meta">
				<span class="comment-written-by">${comment.writtenBy.names}</span>
				<span class="comment-written-on">${Utils.formatDateWithTime(comment?.dateCreated)}</span>
			</div>
			<div class="comment-content">${comment.content}</div>
		</li>
	</g:each>
</ul>