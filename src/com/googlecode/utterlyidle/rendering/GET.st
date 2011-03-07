<form action="$base$$resource.uriTemplate$" method="$resource.method$">
    <p><input type="submit" value="$resource.method$"/> $base$$resource.uriTemplate$ </p>
    $resource.query:{ query |
        $query.keys:{ key |
            $query.(key):{ values |
                $values:{ value | <label for="$key$">$key$:</label><input id="$key$" name="$key$" value="$value$"/> }$
            }$
        }$
    }$

</form>