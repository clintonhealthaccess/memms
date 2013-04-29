<div class="entity-list">
  <!-- List Top Header Template goes here -->
  <div class="heading1-bar">
    <h1>Detailed Reports</h1>
  </div>
  <!-- End of template -->

  <!-- Filter template if any goes here -->
  <g:if test="${filterTemplate!=null}">
    <g:render template="/entity/${filterTemplate}" />
  </g:if>
  <!-- End of template -->

  <!-- List Template goes here -->
  <div id ="list-grid" class="v-tabs">

    <!-- Create Customized Report Template goes here -->
    <g:render template="/entity/reports/createCustomizedReport" />

    <ul id='js-top-tabs' class="v-tabs-nav left">
      <li><a class="active" id="#js-inventory">Inventory</a></li>
      <li><a id="#js-corrective">Corrective Maintenance</a></li>
      <li><a id="#js-preventive">Preventive Maintenance</a></li>
      <li><a id="#js-spare-parts">Spare Parts</a></li>
    </ul>

    <div id='js-inventory' class="v-tabs-content right shown">
      <ul class="v-tabs-subnav">
        <li><a class="active" href="#" id="report-1">First predefined report</a></li>
        <li><a href="#" id="report-2">Second predefined report</a></li>
        <li><a href="#" id="report-3">Third predefined report</a></li>
        <li><a href="#" id="report-4">Fourth predefined report</a></li>
        <li><a href="#" id="report-5">Fifth predefined report</a></li>
        <li><a href="#" id="report-6">Sixth predefined report</a></li>
      </ul>

        <div id='report-1' class="js-shown-report">
          <div class="v-tabs-summary">
            <h2>Summary</h2>
            <hr />
            <ul>
              <li><a href="#">32.020<span>Some label</span></a></li>
              <li><a href="#">12.345<span>Some longer label</span></a></li>
              <li><a href="#">75.501<span>Some very long label</span></a></li>
              <li><a href="#">12.944<span>Some label</span></a></li>
              <li><a href="#">22.300<span>Some long label</span></a></li>
              <li><a href="#">11.478<span>Some very long label</span></a></li>
            </ul>
          </div>

          <div class="v-tabs-criteria">
            <ul class="left">
              <li>
                <span>Report type:</span>
                <a href="#">Lorem Ipsum 1234</a>
              </li>
              <li>
                <span>Report subtype:</span>
                <a href="#">Dolor Sit Amet 1234</a>
              </li>
              <li>
                <span>Ordering:</span>
                <a href="#">by Location</a>
              </li>
              <li>
                <span>Filters:</span>
                <a href="#">There are <a href="#" id='js-filters-toggle' class="tooltip" original-title="click to view them all">133</a> filters applied</a>
              </li>
            </ul>
          </div>

          <div class="filters main">
            <form class="filters-box" method="get" action="/memms/equipmentView/filter" style="display: none;">
              <a href="#" class='filters-close' id='js-filters-close'>
                <img src="${resource(dir:'images',file:'icon_close.png')}" />
              </a>
              <ul class="applied-filters-list">
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>

              </ul>
            </form>
          </div>

          <table class="items spaced ralign">
            <thead>
              <tr>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
              </tr>
              <tr>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
              </tr>
              <tr>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
              </tr>
              <tr>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
              </tr>
              <tr>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
              </tr>
            </tbody>
          </table>
          <ul class="paginate">
            <li><a href="#">Previous</a></li>
            <li><a href="#">1</a></li>
            <li><a class="active" href="#">2</a></li>
            <li><a href="#">3</a></li>
            <li><a href="#">4</a></li>
            <li><a href="#">5</a></li>
            <li><a href="#">6</a></li>
            <li><a href="#">Next</a></li>
          </ul>
        </div>
        <div id='report-2'>
          <p>
            Second predefined report
          </p>
        </div>
        <div id='report-3'>
          <p>
            Third predefined report
          </p>
        </div>
        <div id='report-4'>
          <p>
            Fourth predefined report
          </p>
        </div>
        <div id='report-5'>
          <p>
            Fifth predefined report
          </p>
        </div>
        <div id='report-6'>
          <p>
            Sixth predefined report
          </p>
        </div>
      </div> <!-- end of Inventory -->

      <div id="js-corrective" class="v-tabs-content right">
      <ul class="v-tabs-subnav">
        <li><a class="active" href="#" id="report-1">First predefined report</a></li>
        <li><a href="#" id="report-2">Second predefined report</a></li>
        <li><a href="#" id="report-3">Third predefined report</a></li>
      </ul>

        <div id='report-1' class="js-shown-report">
          <div class="v-tabs-summary">
            <h2>Summary</h2>
            <hr />
            <ul>
              <li><a href="#">32.020<span>Some label</span></a></li>
              <li><a href="#">12.345<span>Some longer label</span></a></li>
              <li><a href="#">75.501<span>Some very long label</span></a></li>
              <li><a href="#">12.944<span>Some label</span></a></li>
              <li><a href="#">22.300<span>Some long label</span></a></li>
              <li><a href="#">11.478<span>Some very long label</span></a></li>
            </ul>
          </div>

          <div class="v-tabs-criteria">
            <ul class="left">
              <li>
                <span>Report type:</span>
                <a href="#">Lorem Ipsum 1234</a>
              </li>
              <li>
                <span>Report subtype:</span>
                <a href="#">Dolor Sit Amet 1234</a>
              </li>
              <li>
                <span>Ordering:</span>
                <a href="#">by Location</a>
              </li>
              <li>
                <span>Filters:</span>
                <a href="#">There are <a href="#" id='js-filters-toggle' class="tooltip" original-title="click to view them all">133</a> filters applied</a>
              </li>
            </ul>
          </div>

          <div class="filters main">
            <form class="filters-box" method="get" action="/memms/equipmentView/filter" style="display: none;">
              <a href="#" class='filters-close' id='js-filters-close'>
                <img src="${resource(dir:'images',file:'icon_close.png')}" />
              </a>
              <ul class="applied-filters-list">
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>
                <li>
                  <h3>Filters category</h3>
                  <ul class="applied-filters">
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                    <li>Lorem ipsum dolor</li>
                  </ul>
                </li>

              </ul>
            </form>
          </div>

          <table class="items spaced ralign">
            <thead>
              <tr>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
                <th>Column name</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
              </tr>
              <tr>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
              </tr>
              <tr>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
              </tr>
              <tr>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
              </tr>
              <tr>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
                <td>12.345</td>
              </tr>
            </tbody>
          </table>
          <ul class="paginate">
            <li><a href="#">Previous</a></li>
            <li><a href="#">1</a></li>
            <li><a class="active" href="#">2</a></li>
            <li><a href="#">3</a></li>
            <li><a href="#">4</a></li>
            <li><a href="#">5</a></li>
            <li><a href="#">6</a></li>
            <li><a href="#">Next</a></li>
          </ul>
        </div>
        <div id='report-2'>
          <p>
            Second predefined report
          </p>
        </div>
        <div id='report-3'>
          <p>
            Third predefined report
          </p>
        </div>
      </div> <!-- end of Corrective Maintenance -->

      <div id="js-preventive" class="v-tabs-content right">
        <p>
          Preventive Maintenance
        </p>
      </div> <!-- end of Preventive Maintenance -->

      <div id="js-spare-parts" class="v-tabs-content right">
        <p>
          Spare Parts
        </p>
      </div> <!-- end of Spare parts -->
  </div>
  <!-- End of template -->
</div>
