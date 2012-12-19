<form action="$resource.path$" method="$resource.method$">
    <span class="method">GET</span>
    $resource.path$?$resource.query.keys:{ key |
                $resource.query.(key):{ value |<label for="$key$">$key$</label>=<input id="$key$" name="$key$" value="$value$" type="text" class="text"/>&amp;}$}$
    <input type="submit" value="Submit"/>
</form>