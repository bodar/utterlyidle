<html>
<head>
    <title>$response.title$</title>
    $response.head$
</head>
<body>
$response.body$
<div>
    $if(!requests.empty)$
    <table class="zebra-striped">
        <thead>
        <tr>
            <th class="header">#</th>
            <th class="header">Uri</th>
            <th class="header">Status</th>
            <th class="header">Milliseconds</th>
        </tr>
        </thead>
        <tbody>
        $requests : {
        <tr>
            <td>$i$</td>
            <td><a href="$it.link$">$it.uri$</a></td>
            <td>$it.status$</td>
            <td>$it.milliseconds$</td>
        </tr>
        }$
        </tbody>
    </table>
    $endif$
    $if(!queries.empty)$
    <table class="zebra-striped">
        <thead>
        <tr>
            <th class="header">#</th>
            <th class="header">Type</th>
            <th class="header">Expression</th>
            <th class="header">Milliseconds</th>
            <th class="header">Count</th>
        </tr>
        </thead>
        <tbody>
        $queries : {
        <tr>
            <td>$i$</td>
            <td>$it.type$</td>
            <td>$it.expression$</td>
            <td>$it.milliseconds$</td>
            <td>$it.count$</td>
        </tr>
        }$
        </tbody>
    </table>
    $endif$
</div>
</body>
</html>