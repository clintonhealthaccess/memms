<!-- Load jQueryUI -->
<script src="http://code.jquery.com/ui/1.10.2/jquery-ui.js"></script>

<!-- Load jQueryUI Dialog Box overlay -->
<script>
  $(document).ready(function(){

    /* Customized reports form steps */

    $( "#js-customize-toggle").click(function(e) {
        e.preventDefault();
      $( "#dialog-form" ).dialog({ modal: true });
      $('.ui-dialog').resizable('destroy');
    });

    var step_counter = 1;
    $('#js-step-2, #js-step-3, #js-step-4').hide();

    $('#js-next-step-1, #js-next-step-2, #js-next-step-3, #js-next-step-4').click(function(e) {
      e.preventDefault();
      if(step_counter < 4) {
        $('#js-step-' + step_counter).hide();
        $('#js-step-' + (step_counter + 1)).slideToggle();
        step_counter++;
      }
    });

    $('#js-prev-step-1, #js-prev-step-2, #js-prev-step-3, #js-prev-step-4').click(function(e) {
      e.preventDefault();
      if(step_counter > 1) {
        $('#js-step-' + step_counter).hide();
        $('#js-step-' + (step_counter - 1)).slideToggle();
        step_counter--;
      }
    });

    /* Select/deselect all dropdown */

    $('.js-select-all').click(function(e) {
      var options = $(this).parents('li').find('select').find('option');
      var select = $(this).parents('li').find('select')
      if($(this).is(':checked')) {
        options.prop('selected', true);
      }else{
        options.prop('selected', false);
      }
      select.trigger('liszt:updated');
    });

    /* Subnavigation */

    $('.v-tabs-subnav a').click(function(e) {
      e.preventDefault();
      $(this).parents('ul').find('.active').removeClass('active');
      $(this).addClass('active');
      var id = $(this).attr('id');
      $(this).parents('div').children('.js-shown-report').hide().removeClass('js-shown-report');
      $(this).parents('div').children('div#' + id).show().addClass('js-shown-report');
    });

    /* Filters show/hide */

    $('#js-filters-toggle').click(function(e) {
      e.preventDefault();
      $('.filters-box').slideToggle();
    });

    $('#js-filters-close').click(function(e) {
      e.preventDefault();
      $('.filters-box').slideToggle();
    });

    /* Top tabs navigation */
    $('#js-top-tabs a').click(function(e){
      e.preventDefault();
      $(this).parents('ul').find('.active').removeClass('active');
      $(this).addClass('active');
      var id = $(this).attr('id');
      $('div.shown').hide().removeClass('shown');
      $('div#' + id).show().addClass('shown');
    });

  });
</script>

<!-- Load JQueryUI Time picker -->
<script>
$(function() {
$( ".js-date-picker" ).datepicker();
});
</script>

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
    <!-- <div class="spinner-container">
      <img src="${resource(dir:'images',file:'list-spinner.gif')}" class="ajax-big-spinner"/>
    </div> -->
    <div class="custom">
      <a id="js-customize-toggle" class="btn gray medium" href="#">Create customized report</a>
    </div>

    <div id="dialog-form">

      <!-- Step 1 -->
      <div class="dialog-form step-1" id='js-step-1'>
        <h2>Select the type of customized report<span class="right">Step <b>1</b> of <b>4</b></span></h2>
        <form>
        <fieldset>
          <ul>
            <li>
              <label for="report_type">Report Type</label>
              <select><option>Please select</option></select>
            </li>
            <li>
              <label for="report_type">Report Subtype</label>
              <select><option>Please select</option></select>
            </li>
          </ul>
        </fieldset>
        <a href="#" class="ui-widget-next right btn" id='js-next-step-1'>Next step</a>
        </form>
      </div>
      <!-- end step 1 -->

      <!-- Step 2 -->
      <div class="dialog-form step-2" id='js-step-2'>
        <h2>Apply filters<span class="right">Step <b>2</b> of <b>4</b></span></h2>
        <p>Type: Inventory > List of inventory</p>
        <form>
        <fieldset>
          <ul>
            <li>
              <label>Facility</label>
              <select multiple style="width:384px;" class="chzn-select">
                <option>Please select</option>
                <option>Please select</option>
                <option>Please select</option>
                <option>Please select</option>
                <option>Please select</option>
              </select>
              <input type='checkbox' class='js-select-all'><span>Select all</span>
            </li>
            <li>
              <label>Department</label>
              <select multiple style="width:384px;" class="chzn-select"><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option></select>
            <input type='checkbox' class='js-select-all'><span>Select all</span>
            </li>
            <li>
              <label>Equipment type</label>
              <select style="width:384px;" class="chzn-select"><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option></select>
          </li>
            <li>
              <label>Equipment status</label>
              <select style="width:384px;" class="chzn-select"><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option><option>Please select</option></select>
              <input type="checkbox" class="no-margin"><span>Show only obsolete equipment</span>
            </li>
            <li>
              <label for="report_type">Acquisition period</label>
              <input class="js-date-picker date-picker idle-field" /> <span class="dash">-</span> <input class="js-date-picker date-picker idle-field" />
            </li>
            <li>
              <label for="report_type">Which cost</label>
              <input class="js-date-picker date-picker idle-field" /> <span class="dash">-</span> <input class="js-date-picker date-picker idle-field" />
              <input type="checkbox"><span>Include equipment without cost</span>
            </li>
            <li>
              <label>Warranty</label>
              <input type="checkbox"><span>Under warranty</span>
            </li>
          </ul>
          <!-- <label for="name">Name</label>
          <input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all" />
          <label for="email">Email</label>
          <input type="text" name="email" id="email" value="" class="text ui-widget-content ui-corner-all" />
          <label for="password">Password</label>
          <input type="password" name="password" id="password" value="" class="text ui-widget-content ui-corner-all" /> -->
        </fieldset>
        <a href="#" class="ui-widget-previous left btn gray medium" id='js-prev-step-2'>Previous step</a>
        <a href="#" class="ui-widget-next right btn" id='js-next-step-2'>Next step</a>
        </form>
      </div>
      <!-- end step 2 -->

      <!-- Step 3 -->
      <div class="dialog-form step-3" id='js-step-3'>
        <h2>Choose the order<span class="right">Step <b>3</b> of <b>4</b></span></h2>
        <p>Type: Inventory > List of inventory</p>
        <form>
          <fieldset>
            <ul>
              <li>
                <label for="report_type">Order by</label>
                <select><option>Please select</option></select>
              </li>
            </ul>
          </fieldset>
        </form>
        <a href="#" class="ui-widget-previous left btn gray medium" id='js-prev-step-3'>Previous step</a>
        <a href="#" class="ui-widget-next right btn" id='js-next-step-3'>Next step</a>
      </div>
      <!-- end step 3 -->

      <!-- Step 4 -->
      <div class="dialog-form step-4" id='js-step-4'>
        <h2>Select information to present<span class="right">Step <b>4</b> of <b>4</b></span></h2>
        <p>Type: Inventory > List of inventory</p>
        <form>
          <fieldset>
            <ul class="twocol-checkboxes">
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
              <li><input type="checkbox"><span>Lorem ipsum dolor sit amet</span></li>
            </ul>
          </fieldset>
        </form>
        <a href="#" class="ui-widget-previous left btn gray medium" id='js-prev-step-4'>Previous step</a>
        <a href="#" class="ui-widget-next right btn" id='js-next-step-4'>Submit</a>
      </div>
    </div>
    <!-- end step 4 -->

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
            <!-- Load & initialize Tipsy -->
            <script src="/memms/static/js/jquery/tipsy/src/javascripts/jquery.tipsy.js" type="text/javascript" ></script>
            <script src="/memms/static/js/tipsy_init.js" type="text/javascript" ></script>
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
            <script src="/memms/static/js/form_init.js" type="text/javascript" ></script>
            <script src="/memms/static/js/dashboard/foldable.js" type="text/javascript" ></script>
            <script src="/memms/static/js/dashboard/list_tabs.js" type="text/javascript" ></script>

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
            <!-- Load & initialize Tipsy -->
            <script src="/memms/static/js/jquery/tipsy/src/javascripts/jquery.tipsy.js" type="text/javascript" ></script>
            <script src="/memms/static/js/tipsy_init.js" type="text/javascript" ></script>
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
            <script src="/memms/static/js/form_init.js" type="text/javascript" ></script>
            <script src="/memms/static/js/dashboard/foldable.js" type="text/javascript" ></script>
            <script src="/memms/static/js/dashboard/list_tabs.js" type="text/javascript" ></script>

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

<script src="../temp_reports/chosen.js"></script>


<script type="text/javascript">
  $(".chzn-select").chosen();
</script>
