<!DOCTYPE html>
<!-- This code is for demonstration purposes only.  You should not hotlink to Github, Rawgit, or files from the Cytoscape.js documentation in your production apps. -->
<html lang="en">
<head>
    <link href="../css/style.css" rel="stylesheet" />
    <meta charset=utf-8 />
    <meta name="viewport" content="user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, minimal-ui">
    <title>调用链拓扑图</title>
    <script src="../js/cytoscape.min.js"></script>
</head>
<body>
<div id="cy"></div>
<!-- Load application code at the end to ensure DOM is loaded -->
<script>

    fetch('/graph/detail/a234').then(function (response) {
        return response.json();
    }).then(function (json) {

        var cy = cytoscape({
            container: document.getElementById('cy'),
            boxSelectionEnabled: false,
            autounselectify: true,
            elements: json,

            layout: {
                name: 'breadthfirst',
                directed: true,
                roots: '#a',
                padding: 10
            }
        });

        json = {
            nodes: [
                { data: { id: 'a' } },
                { data: { id: 'b' } },
                { data: { id: 'c' } },
                { data: { id: 'd' } },
                { data: { id: 'e' } }
            ],

            edges: [
                { data: { id: 'a"e', weight: 1, source: 'a', target: 'e' } },
                { data: { id: 'ab', weight: 3, source: 'a', target: 'b' } },
                { data: { id: 'be', weight: 4, source: 'b', target: 'e' } },
                { data: { id: 'bc', weight: 5, source: 'b', target: 'c' } },
                { data: { id: 'ce', weight: 6, source: 'c', target: 'e' } },
                { data: { id: 'cd', weight: 2, source: 'c', target: 'd' } },
                { data: { id: 'de', weight: 7, source: 'd', target: 'e' } }
            ]
        }
    })




</script>
</body>
</html>
