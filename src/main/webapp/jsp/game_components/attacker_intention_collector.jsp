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
<script>
    var attackButton = document.getElementById('submitMutant');
    var theForm = document.getElementById('atk');

    // Who calls this function ?
    function updateAttackForm(value){
        document.getElementById("attacker_intention").value = value;
        /* progressBar(); */
        // Disabled for #490
        /* registerMutantProgressBar(); */
        theForm.submit();
        attackButton.disabled = true;
    }

    var input = document.createElement("input");
    input.setAttribute("type", "hidden");
    input.setAttribute("id", "attacker_intention");
    input.setAttribute("name", "attacker_intention");
    input.setAttribute("value", "");
    theForm.appendChild(input);

    // Replace the submit button with the following code:
    var originalAttachButton = document.getElementById("submitMutant");

    // Create the attackDropDown
    var attackDropDown = document.createElement('div');
    attackDropDown.setAttribute("id", "attackDropDown")
    attackDropDown.setAttribute("class", "dropdown")
    attackDropDown.setAttribute("style", "float: right; margin-right: 5px")

    var attackDropDownButton = document.createElement('button');
    attackDropDownButton.setAttribute("type", "button")
    attackDropDownButton.setAttribute("class", "btn btn-primary btn-game btn-right dropdown-toggle")
    attackDropDownButton.setAttribute("data-toggle", "dropdown")
    // Note the ID !
    attackDropDownButton.setAttribute("id", "submitMutantTemp" )
    attackDropDownButton.setAttribute("form", "atk" )
    attackDropDownButton.setAttribute("aria-haspopup", "true" )
    attackDropDownButton.setAttribute("aria-expanded", "false" )
    attackDropDownButton.innerHTML="Attack <span class=\"glyphicon glyphicon-triangle-bottom\" style=\"font-size: small\"/></span>";
    //
    attackDropDown.appendChild(attackDropDownButton)

    <% if (game.getState() != GameState.ACTIVE) { %>
    attackDropDownButton.disabled = true;
    <% } %>

    // At this point we replace originalAttachButton
    originalAttachButton.parentNode.replaceChild(attackDropDown, originalAttachButton);

    // Finally we adjust the id of the submitMutantTemp button
    attackDropDownButton.setAttribute("id", "submitMutant")

    // create <ul> element containing list of intention options
    var intentionList = document.createElement('ul');
    intentionList.setAttribute("class", "dropdown-menu dropdown-menu-right");
    intentionList.setAttribute("aria-labelledby", "submitMutant");
    // append <ul> to dropDown attack button
    attackDropDown.appendChild(intentionList);

    var killableListItem = document.createElement('li');
    var killableMutant = document.createElement('a');
    killableMutant.setAttribute("class", "dropdown-item");
    killableMutant.setAttribute("style", "cursor: pointer");
    killableMutant.setAttribute("onclick", "updateAttackForm(\'KILLABLE\')");
    killableMutant.innerHTML = 'My mutant is killable';
    // connect <ul>, <li> and <a> elements
    intentionList.appendChild(killableListItem);
    killableListItem.appendChild(killableMutant);

    var equivalentListItem = document.createElement('li');
    var equivalentMutant = document.createElement('a');
    equivalentMutant.setAttribute("class", "dropdown-item");
    equivalentMutant.setAttribute("style", "cursor: pointer");
    equivalentMutant.setAttribute("onclick", "updateAttackForm(\'EQUIVALENT\')");
    equivalentMutant.innerHTML = 'My mutant is equivalent';
    // connect <ul>, <li> and <a> elements
    intentionList.appendChild(equivalentListItem);
    equivalentListItem.appendChild(equivalentMutant);

    var unknownListItem = document.createElement('li');
    var unknownMutant = document.createElement('a');
    unknownMutant.setAttribute("class", "dropdown-item");
    unknownMutant.setAttribute("style", "cursor: pointer");
    unknownMutant.setAttribute("onclick", "updateAttackForm(\'DONTKNOW\')");
    unknownMutant.innerHTML = 'I don\'t know if my mutant is killable';
    // connect <ul>, <li> and <a> elements
    intentionList.appendChild(unknownListItem);
    unknownListItem.appendChild(unknownMutant);
</script>
