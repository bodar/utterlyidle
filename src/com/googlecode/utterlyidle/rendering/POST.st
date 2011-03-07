<form action="$base$$resource.uriTemplate$" method="$resource.method$">
    <p><input type="submit" value="$resource.method$"/> $base$$resource.uriTemplate$ </p>
    $resource.form:{ form |
        $form.keys:{ key |
            $form.(key):{ values |
                $values:{ value | <label for="$key$">$key$:</label><input id="$key$" name="$key$" value="$value$"/> }$
            }$
        }$
    }$
</form>