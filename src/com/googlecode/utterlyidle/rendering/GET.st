<form action="$base$$resource.uriTemplate$" method="$resource.method$">
    <p><span class="method">$resource.method$</span> $base$$resource.uriTemplate$</p>
    $resource.query:{ query |
        $query.keys:{ key |
            $query.(key):{ values |
                $values:{ value | <label for="$key$">$key$:</label><input id="$key$" name="$key$" value="$value$"/> }$
            }$
        }$
    }$
    <input type="submit" value="Submit"/>
</form>