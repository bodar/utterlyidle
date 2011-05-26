<form action="$base$$resource.uriTemplate$" method="$resource.method$">
    <p><span class="method">$resource.method$</span> $base$$resource.uriTemplate$</p>
    $resource.form:{ form |
        $form.keys:{ key |
            $form.(key):{ values |
                $values:{ value | <label for="$key$">$key$:</label><input id="$key$" name="$key$" value="$value$" class="text"/> }$
            }$
        }$
    }$
    <input type="submit" value="Submit"/>
</form>