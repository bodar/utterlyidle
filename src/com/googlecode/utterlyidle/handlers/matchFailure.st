<html>
    <head>
        <title>$status$</title>
        <script type="text/javascript" src="$base$utterlyidle/handlers/matchFailure.js"></script>
    </head>
<body>
    <h1>$status$</h1>
    <p>Did you mean one of these?</p>

    <ul class="resources">
        $requests:{ request | <li class="resource">$GET()$</li> }$
    </ul>


</body>
</html>