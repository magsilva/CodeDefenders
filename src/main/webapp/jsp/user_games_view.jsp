<%--

    Copyright (C) 2016-2019 Code Defenders contributors

    This file is part of Code Defenders.

    Code Defenders is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or (at
    your option) any later version.

    Code Defenders is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Code Defenders. If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page import="org.codedefenders.game.GameState" %>
<%@ page import="java.util.List" %>
<%@ page import="org.codedefenders.model.UserMultiplayerGameInfo" %>
<%@ page import="org.codedefenders.model.Player" %>
<%@ page import="org.codedefenders.game.multiplayer.PlayerScore" %>
<% String pageTitle= null ; %>
<%@ include file="/jsp/header_main.jsp" %>
<%
    // Games active for this user (Created or joined)
    List<UserMultiplayerGameInfo> activeGames = ((List<UserMultiplayerGameInfo>) request.getAttribute("activeGames"));

    // Games open for this user (not created or joined, and enough space for one player)
    List<UserMultiplayerGameInfo> openGames = ((List<UserMultiplayerGameInfo>) request.getAttribute("openGames"));

    boolean gamesJoinable = (boolean) request.getAttribute("gamesJoinable");
    boolean gamesCreatable = (boolean) request.getAttribute("gamesCreatable");
%>

<div class="w-100">
<h2 class="full-width page-title">My Games</h2>
<table id="my-games" class="table table-striped table-hover table-responsive table-paragraphs games-table">
	<tr>
		<th>ID</th>
		<th>Creator</th>
		<th>Class</th>
		<th>Attackers</th>
		<th>Defenders</th>
		<th>Level</th>
		<th></th>
	</tr>
<%
	if (activeGames.isEmpty()) {
%>
	<tr><td colspan="100%"> You are currently not active in any game.</td></tr>
<%
	} else {
		for (UserMultiplayerGameInfo info : activeGames) {
            int gameId = info.gameId();
            List<Player> attackers = info.attackers();
            List<Player> defenders = info.defenders();
%>
	<tr id="<%="game-"+gameId%>">
		<td class="col-sm-1"><%= gameId %></td>
		<td class="col-sm-1"><%=info.creatorName()%></td>
		<td class="col-sm-2">
			<a href="#" data-toggle="modal" data-target="#modalCUTFor<%=gameId%>"><%=info.cutAlias()%></a>
			<div id="modalCUTFor<%=gameId%>" class="modal fade" role="dialog" style="text-align: left;" >
				<div class="modal-dialog">
					<!-- Modal content-->
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal">&times;</button>
							<h4 class="modal-title"><%=info.cutAlias()%></h4>
						</div>
						<div class="modal-body">
							<pre class="readonly-pre"><textarea class="readonly-textarea classPreview"
                                                                id="sut<%=gameId%>"
                                                                name="cut<%=gameId%>" cols="80"
                                                                rows="30"><%=info.cutSource()%></textarea></pre>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
						</div>
					</div>
				</div>
			</div>
		</td>
        <td class="col-sm-1">
            <span><%=attackers.size()%></span>
            <%
                if (!attackers.isEmpty()) {
            %>
            <br>
            <a href="#" data-toggle="modal" data-target="#modalAttackersFor<%=gameId%>">(Details)</a>
            <div id="modalAttackersFor<%=gameId%>" class="modal fade" role="dialog" style="text-align: left;" >
                <div class="modal-dialog">
                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 class="modal-title">Attackers</h4>
                        </div>
                        <div class="modal-body">
                            <table id="game-<%=gameId%>-attackers" class="table table-striped table-hover table-responsive table-paragraphs games-table">
                                <tr>
                                    <th>Name</th>
                                    <th>Points</th>
                                </tr>
                            <%
                                for (Player attacker : attackers) {
                            %>
                                <tr>
                                    <td class="col-sm-1"><%=attacker.getUser().getUsername()%></td>
                                    <td class="col-sm-1"><%=attacker.getPoints()%></td>
                                </tr>
                            <%
                                }
                            %>
                            </table>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>
        </td>
        <td class="col-sm-1">
            <span><%=defenders.size()%> </span>
            <%
                if (!defenders.isEmpty()) {
            %>
            <br>
            <a href="#" data-toggle="modal" data-target="#modalDefendersFor<%=gameId%>">(Details)</a>
            <div id="modalDefendersFor<%=gameId%>" class="modal fade" role="dialog" style="text-align: left;" >
                <div class="modal-dialog">
                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 class="modal-title">Defenders</h4>
                        </div>
                        <div class="modal-body">
                            <table id="game-<%=gameId%>-defenders" class="table table-striped table-hover table-responsive table-paragraphs games-table">
                                <tr>
                                    <th>Name</th>
                                    <th>Points</th>
                                </tr>
                                <%
                                    for (Player defender : defenders) {
                                %>
                                <tr>
                                    <td class="col-sm-1"><%=defender.getUser().getUsername()%></td>
                                    <td class="col-sm-1"><%=defender.getPoints()%></td>
                                </tr>
                                <%
                                    }
                                %>
                            </table>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>
        </td>
		<td class="col-sm-1"><%=info.gameLevel().getFormattedString()%></td>
		<td class="col-sm-2">
            <%
                if (info.gameState() == GameState.CREATED && info.creatorId() == info.userId()) {
            %>
            <form id="adminStartBtn-<%=gameId%>" action="<%=request.getContextPath() + Paths.BATTLEGROUND_SELECTION%>"
                  method="post">
                <button type="submit" class="btn btn-sm btn-primary" id="startGame-<%=gameId%>"
                        form="adminStartBtn-<%=gameId%>">
                    Start Game
                </button>
                <input type="hidden" name="formType" value="startGame">
                <input type="hidden" name="gameId" value="<%= gameId %>"/>
            </form>
            <%
                } else {
                    switch (info.userRole()) {
                        case ATTACKER:
                            if (info.gameState() != GameState.CREATED) {
            %>
            <a class="btn btn-sm btn-primary" id="<%="attack-"+gameId%>"
               style="background-color: #884466;border-color: #772233;"
               href="<%= request.getContextPath()  + Paths.BATTLEGROUND_GAME%>?gameId=<%= gameId %>">Attack</a>
            <%
                            } else {
            %>
            Joined as Attacker
            <% if (gamesJoinable) { %>
            <form id="attLeave" action="<%= request.getContextPath()  + Paths.BATTLEGROUND_SELECTION%>" method="post">
                <input class="btn btn-sm btn-danger" type="hidden" name="formType" value="leaveGame">
                <input type="hidden" name="gameId" value="<%=gameId%>">
                <button class="btn btn-sm btn-danger" id="<%="leave-attacker-"+gameId%>" type="submit" form="attLeave"
                        value="Leave">
                    Leave
                </button>
            </form>
            <% } %>
            <%
                            }
                            break;
                        case DEFENDER:
                    if (info.gameState() != GameState.CREATED) {
            %>
            <a class="btn btn-sm btn-primary" id="<%="defend-"+gameId%>"
               style="background-color: #446688;border-color: #225577"
               href="<%= request.getContextPath()  + Paths.BATTLEGROUND_GAME%>?gameId=<%= gameId %>">Defend</a>
            <%
            } else {
            %>
            Joined as Defender
            <%if (gamesJoinable) { %>
            <form id="defLeave" action="<%= request.getContextPath()  + Paths.BATTLEGROUND_SELECTION%>" method="post">
                <input class="btn btn-sm btn-danger" type="hidden" name="formType" value="leaveGame">
                <input type="hidden" name="gameId" value="<%=gameId%>">
                <button class="btn btn-sm btn-danger" id="<%="leave-defender-"+gameId%>" type="submit" form="defLeave"
                        value="Leave">
                    Leave
                </button>
            </form>
            <% } %>
            <%
                    }
                    break;
                case OBSERVER:
                    if (info.creatorId() == info.userId()) {
            %>
            <a class="btn btn-sm btn-primary" id="<%="observe-"+gameId%>"
               href="<%= request.getContextPath()  + Paths.BATTLEGROUND_GAME%>?gameId=<%= gameId %>">
                Observe
            </a>
            <%
                            }
                     break;
                 default:
                     break;
                    }
                }
            %>
		</td>
	</tr>
<%
		} // Closes FOR
	} // Closes ELSE
%>

</table>
<%
%>

	<%if (gamesCreatable) { %>
	<a id="createBattleground" class = "btn btn-primary" href="<%=request.getContextPath() + Paths.BATTLEGROUND_CREATE%>">Create Battleground</a>
	<%}%>

<%if (gamesJoinable) { %>
<h2 class="full-width page-title">Open Games</h2>
<table id="tableOpenGames" class="table table-hover table-responsive table-paragraphs games-table">
	<tr>
		<th>ID</th>
		<th>Creator</th>
		<th>Class</th>
		<th>Attackers</th>
		<th>Defenders</th>
		<th>Level</th>
	</tr>
<%
	if (openGames.isEmpty()) {
%>
	<tr><td colspan="100%"> There are currently no open games. </td></tr>
<%
	} else {
        for (UserMultiplayerGameInfo info : openGames) {
            int gameId = info.gameId();
            List<Player> attackers = info.attackers();
            List<Player> defenders = info.defenders();
            Map<Integer, PlayerScore> attackerScores = info.getMutantScores();
            Map<Integer, PlayerScore> defenderScores = info.getTestScores();
%>
		<tr id="game-<%=gameId%>" class="toggle-details">
			<td class="col-sm-1"><%=gameId%></td>
			<td class="col-sm-1"><%=info.creatorName()%></td>
			<td class="col-sm-2">
				<a href="#" data-toggle="modal" data-target="#modalCUTFor<%=gameId%>">
					<%=info.cutAlias()%>
				</a>
				<div id="modalCUTFor<%=gameId%>" class="modal fade" role="dialog" style="text-align: left;" >
					<div class="modal-dialog">
						<!-- Modal content-->
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal">&times;</button>
								<h4 class="modal-title"><%=info.cutAlias()%></h4>
							</div>
							<div class="modal-body">
                                <pre class="readonly-pre"><textarea
										class="readonly-textarea classPreview"
										id="sut<%=gameId%>"
										name="cut<%=gameId%>" cols="80"
										rows="30"><%=info.cutSource()%></textarea></pre>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
							</div>
						</div>
					</div>
				</div>
			</td>
            <td class="col-sm-2">
                <form id="joinGameForm_attacker_<%=gameId%>"
                      action="<%=request.getContextPath() + Paths.BATTLEGROUND_SELECTION%>" method="post">
                    <input type="hidden" name="formType" value="joinGame">
                    <input type="hidden" name="gameId" value=<%=info.gameId()%>>
                    <input type="hidden" name="attacker" value=1>
                    <%=attackers.size()%>
                    <button type="submit" id="<%="join-attacker-"+info.gameId()%>" class="btn btn-primary btn-sm"
                            style="background-color: #884466;border-color: #772233; margin: 0 0 0 5px; padding: 3px 7px;"
                            value="Join as Attacker">Join
                    </button>
                </form>
            </td>
            <td class="col-sm-2">
                <form id="joinGameForm_defender_<%=gameId%>"
                      action="<%=request.getContextPath() + Paths.BATTLEGROUND_SELECTION%>" method="post">
                    <input type="hidden" name="formType" value="joinGame">
                    <input type="hidden" name="gameId" value=<%=gameId%>>
                    <input type="hidden" name="defender" value=1>
                    <%=defenders.size() %>
                    <button type="submit" id="<%="join-defender-"+gameId%>" class="btn btn-primary btn-sm"
                            style="background-color: #446688;border-color: #225577; margin: 0 0 0 5px; padding: 3px 7px;"
                            value="Join as Defender">Join
                    </button>
                </form>
            </td>
            <td class="col-sm-1"><%=info.gameLevel().getFormattedString() %></td>
		</tr>
        <tr id="game-detais-<%=gameId%>" class="game-<%=gameId%>" style="display: none">
            <td colspan="6">
                <table class="table-child-details" style="display: inline; margin-right: 15px">
                    <thead>
                        <tr>
                            <th>
                                User
                            </th>
                            <th>
                                Mutants
                            </th>
                            <th>
                                Alive
                            </th>
                            <th>
                                Points
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Player attacker : attackers) {
                            int playerId = attacker.getId();
                            PlayerScore playerScores = attackerScores.get(playerId);
                            boolean scoresExists = attackerScores.containsKey(playerId) && attackerScores.get(playerId) != null;
                            %>
                            <tr>
                                <td>
                                    <%=attacker.getUser().getUsername()%>
                                </td>
                                <td>
                                    <% if (scoresExists) { %>
                                    <%=playerScores.getQuantity() %>
                                    <% } else { %>
                                    0
                                    <% } %>
                                </td>
                                <td>
                                    <% if (scoresExists) { %>
                                    <%-- Well it is a string ... So split it to get the alive Mutants--%>
                                    <%=playerScores.getMutantKillInformation().split("/")[0]%>
                                    <% } else { %>
                                    0
                                    <% } %>
                                </td>
                                <td>
                                    <% if (scoresExists) { %>
                                    <%=playerScores.getTotalScore()%>
                                    <% } else { %>
                                    0
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
                <table class="table-child-details" style="display: inline; margin-left: 15px">
                    <thead>
                        <tr>
                            <th>
                                User
                            </th>
                            <th>
                                Tests
                            </th>
                            <th>
                                Mutants killed
                            </th>
                            <th>
                                Points
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Player defender : defenders) {
                        int playerId = defender.getId();
                        PlayerScore playerScores = defenderScores.get(playerId);
                        boolean scoresExists = defenderScores.containsKey(playerId) && defenderScores.get(playerId) != null;
                    %>
                        <tr>
                            <td>
                                <%=defender.getUser().getUsername()%>
                            </td>
                            <td>
                                <% if (scoresExists) { %>
                                <%=playerScores.getQuantity() %>
                                <% } else { %>
                                0
                                <% } %>
                            </td>
                            <td>
                                <% if (scoresExists) { %>
                                <%=playerScores.getMutantKillInformation()%>
                                <% } else { %>
                                0
                                <% } %>
                            </td>
                            <td>
                                <% if (scoresExists) { %>
                                <%=playerScores.getTotalScore()%>
                                <% } else { %>
                                0
                                <% } %>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </td>

        </tr>
<%
        } // Closes FOR
    } // Closes ELSE
%>
	</table>
	<%}%>

	<script>
		$(document).ready(function() {
			$.fn.dataTable.moment( 'YY/MM/DD HH:mm' );
			$('#tableMPGames').DataTable( {
				"paging":   false,
				"searching": false,
				"order": [[ 5, "asc" ]],
				"language": {
					"info": ""
				}
			} );
		} );

		$('.modal').on('shown.bs.modal', function() {
			var codeMirrorContainer = $(this).find(".CodeMirror")[0];
			if (codeMirrorContainer && codeMirrorContainer.CodeMirror) {
				codeMirrorContainer.CodeMirror.refresh();
			} else {
				var editorDiff = CodeMirror.fromTextArea($(this).find('textarea')[0], {
					lineNumbers: false,
					readOnly: true,
					mode: "text/x-java"
				});
				editorDiff.setSize("100%", 500);
			}
		});

        $('#tableOpenGames tr.toggle-details').on('click', function () {
            var id = '.' + $(this).attr('id');
            if ($(id).is(':visible')) {
                $(id).hide()
            } else {
                $(id).show()
            }
        })
	</script>

</div>
<%@ include file="/jsp/footer.jsp" %>
