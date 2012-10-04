<form action="$resource.path$" method="$resource.method$">
    <p><span class="method">$resource.method$</span> $resource.path$</p>
        $resource.form.keys:{ key |
            $resource.form.(key):{ values |
                $values:{ value | <label for="$key$">$key$:</label><input id="$key$" name="$key$" value="$value$" class="text"/> }$
            }$
        }$
    <input type="submit" value="Submit"/>
</form>