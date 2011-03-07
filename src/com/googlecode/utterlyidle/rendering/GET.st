<form action="$base$$resource.uriTemplate$" method="$resource.method$">
    <span class="method">$resource.method$</span>
    $base$$resource.uriTemplate$?$resource.query:{ query |
        $query.keys:{ key |
            $query.(key):{ values |
                $values:{ value |<label for="$key$">$key$</label>=<input id="$key$" name="$key$" value="$value$" type="text" class="text"/>&amp;}$}$}$}$
    <input type="submit" value="Submit"/>
</form>