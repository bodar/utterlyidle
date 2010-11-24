<html>
    <head>
        <title>$status$</title>
        <script type="text/javascript" src="$base$utterlyidle/rendering/matchFailure.js"></script>
    </head>
<body>
    <h1>$status$</h1>
    <p>Did you mean one of these?</p>

    <ul class="resources">
        $resources:{ resource |
            <li class="resource">$resource.method:{ method | $(method)(resource=resource)$ }$</li> 
        }$
    </ul>

</body>
</html>