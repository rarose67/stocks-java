function myFunction(current) {
    var max = document.getElementById("max-items").value;
    var perPage = document.getElementById("per-page-select").value;
    var pageList = document.getElementById("page-list");
    var pLength = pageList.childNodes.length;
    var maxPage = Math.ceil(max / perPage);

    var pagestoShow = 5;
    var firstinGroup = current - Math.floor(pagestoShow / 2);
    var lastinGroup = current + Math.floor(pagestoShow / 2);
    var pageGroups = Math.ceil(maxPage / pagestoShow);

    //window.alert(firstinGroup + ", " + lastinGroup + ", " + pageGroups);

    if(firstinGroup < 1)
    {
        firstinGroup = 1;
        lastinGroup = pagestoShow;
    }

    if(lastinGroup > maxPage)
    {
        lastinGroup = maxPage;
        firstinGroup = (lastinGroup - pagestoShow) + 1;
    }

    if(pageGroups == 1)
    {
        firstinGroup = 1;
        lastinGroup = maxPage;
    }

     getPage(current, perPage, true);


    for(var i = pLength-1; i >= 0; i--)
    {
        pageList.removeChild(pageList.childNodes[i]);
    }

    for(var j=firstinGroup; j <= lastinGroup; j++)
    {
        var newElement = document.createElement('li');
        newElement.setAttribute("id", "page" + j);

        if(j == current)
        {
            newElement.setAttribute("class", "active");
        }

        /** This part of the function is derived from the question on
         https://stackoverflow.com/questions/3495679/passing-parameters-in-javascript-onclick-event
         answered by Jamie Wong
        */
        var link = document.createElement('a');
           link.setAttribute('href', '#');
           link.innerHTML = j;
           link.onclick = (function() {
              var currentJ = j;
              return function() {
                  myFunction(currentJ);
              }
           })();

           newElement.appendChild(link);
           pageList.appendChild(newElement);
    }
}

function getPage(page, perPage, loaded)
{
    if (page < 1)
    {
        page = 1;
    }

    var rows = document.getElementsByClassName("rep-row");
    var i;
    var max = rows.length;
    var rangeStart = ((page - 1) * perPage);
    var rangeEnd = (page * perPage);

    if (rangeEnd > max)
    {
        rangeEnd = max;
    }

    if (loaded)
    {
      for(i=0; i <max; i++)
      {
          rows[i].style="";
      }
    }

     for(i=0; i< max; i++)
     {
        if((i < rangeStart) || (i >= rangeEnd))
        {
            rows[i].style.display='none';
        }
     }
}

function showPercentage() {
    var x = document.getElementById("reinvest");
    if (x.checked)
    {
        document.getElementById("percentage").value = 0;
        document.getElementById("percentage-div").style.display='none';
    }
    else
    {
        document.getElementById("percentage-div").style.display='block';
    }
}

