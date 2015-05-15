<form action="$path$" method="$method$">
    <span class="method">GET</span>
    $path$?$query:{ value, key |
                <label for="$key$">$key$</label>=<input id="$key$" name="$key$" value="$value$" type="text" class="text"/>&amp;}$
    <input type="submit" value="Submit"/>
</form>