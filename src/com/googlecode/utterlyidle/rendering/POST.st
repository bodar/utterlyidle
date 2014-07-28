<form action="$resource.path$" method="$resource.method$">
    <p><span class="method">POST</span> $resource.path$</p>
    <ul>
        $resource.form.keys:{ key |
            <li><label for="$key$">$key$:</label><input id="$key$" name="$key$" value="$resource.form.(key)$" class="text"/></li>
        }$
    </ul>
    <input type="submit" value="Submit"/>
</form>