-- Generated by TestCases
SELECT DOM_TEXT(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.welcome');
SELECT DOM_TEXT(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfPrice', 0, 5);
SELECT DOM_SRC(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfPic img', 0, 5);
SELECT DOM_TITLE(DOM), DOM_ABS_HREF(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfPic a', 0, 5);
SELECT DOM_TITLE(DOM), DOM_ABS_HREF(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), 'a[href~=item]', 0, 5);

-- Generated by TestCases
SELECT * FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '*:in-box(*,*,323,31)');
SELECT * FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '*:in-box(*,*,229,36)', 0, 5);
SELECT IN_BOX_FIRST_TEXT(DOM_LOAD('https://www.mia.com/formulas.html'), '229x36');

-- Generated by TestManual
SELECT
            DOM, DOM_FIRST_HREF(DOM), TOP, LEFT, WIDTH, HEIGHT, CHAR, IMG, A, CHILD, SIBLING
            FROM LOAD_AND_GET_FEATURES('https://www.mia.com/formulas.html', '*:expr(child > 20 && char > 100 && width > 800)')
            ORDER BY CHILD DESC, CHAR DESC LIMIT 50;

-- Generated by TestManual
SELECT
            DOM, DOM_FIRST_HREF(DOM), TOP, LEFT, WIDTH, HEIGHT, CHAR, IMG, A, SIBLING, DOM_TEXT(DOM)
            FROM LOAD_AND_GET_FEATURES('https://www.mia.com/formulas.html', '*:expr(sibling > 20 && char > 40 && char < 100 && width > 200)')
            ORDER BY SIBLING DESC, CHAR DESC LIMIT 500;

-- Generated by TestManual
SELECT DOM_TEXT(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.welcome');
SELECT DOM_TEXT(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfPrice', 0, 5);
SELECT DOM_SRC(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfPic img', 0, 5);
SELECT DOM_TITLE(DOM), DOM_ABS_HREF(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfPic a', 0, 5);
SELECT DOM_TITLE(DOM), DOM_ABS_HREF(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), 'a[href~=item]', 0, 5);

-- Generated by TestManual
SELECT
  DOM_FIRST_TEXT(DOM, 'div:iN-bOx(560,27),*:IN-BOX(560,56)') AS TITLE,
  DOM_FIRST_TEXT(DOM, '*:expr(TOP>=287 && TOP<=307 && LEFT==472 && width==560 && height>=27 && height<=54 && char>=34 && char<=41)') AS TITLE2,
  IN_BOX_FIRST_TEXT(DOM, '560x27,560x56') AS TITLE3
FROM LOAD_OUT_PAGES('https://www.mia.com/formulas.html', '*:expr(img>0 && width>200 && height>200 && sibling>30)', 1, 10)
WHERE DOM_CH(DOM) > 100;

-- Generated by TestManual
SELECT DOM, TOP, LEFT, WIDTH, HEIGHT, IMG, A, SIBLING, DOM_TEXT(DOM), DOM_FIRST_HREF(DOM) FROM LOAD_AND_GET_FEATURES('http://news.cnhubei.com/') WHERE SIBLING>30 AND DOM_TEXT_LENGTH(DOM) > 10 AND TOP > 300 AND TOP < 3000;
SELECT DOM, TOP, LEFT, WIDTH, HEIGHT, IMG, A, CHAR, SIBLING, DOM_TEXT(DOM), DOM_FIRST_HREF(DOM) FROM LOAD_AND_GET_FEATURES('http://news.cnhubei.com/xw/jj/201804/t4102239.shtml') WHERE SEQ > 170 AND SEQ < 400;
SELECT
  DOM_FIRST_TEXT(DOM, 'H1') AS TITLE,
  DOM_FIRST_TEXT(DOM, '.jcwsy_mini_content') AS DATE_TIME,
  DOM_FIRST_TEXT(DOM, '.content_box') AS CONTENT
FROM LOAD_OUT_PAGES('http://news.cnhubei.com/', '.news_list_box', 1, 100);

-- Generated by TestManual
SELECT
  DOM_FIRST_TEXT(DOM, 'div:iN-bOx(560,27),*:IN-BOX(560,56)') AS TITLE,
  DOM_FIRST_TEXT(DOM, '.brand') AS TITLE2,
  DOM_WIDTH(DOM_SELECT_FIRST(DOM, '.brand')) AS W,
  DOM_HEIGHT(DOM_SELECT_FIRST(DOM, '.brand')) AS H,
  IN_BOX_FIRST_TEXT(DOM, '560x27,560x56') AS TITLE3
FROM LOAD_OUT_PAGES('https://www.mia.com/formulas.html', '*:expr(img>0 && width>200 && height>200 && sibling>=40)', 1, 100)
WHERE DOM_CH(DOM) > 100;

-- Generated by TestManual
SELECT
            DOM_PARENT(DOM), DOM, DOM_FIRST_HREF(DOM), TOP, LEFT, WIDTH, HEIGHT, CHAR, IMG, A, SIBLING, DOM_TEXT(DOM)
            FROM LOAD_AND_GET_FEATURES('https://www.mia.com/formulas.html', '*:expr(sibling > 20 && char > 40 && char < 100 && width > 200)')
            ORDER BY SIBLING DESC, CHAR DESC LIMIT 50;

-- Generated by TestManual
SET @LINK='https://www.mia.com/formulas.html';
SET @OUT_LINK_STRICT_CSS='*:expr(img>0 && width>200 && height>200 && sibling>30)';

-- Show page features to see where are the useful links
-- SELECT * FROM DOMT_LOAD_AND_GET_FEATURES(@LINK, @OUT_LINK_STRICT_CSS) LIMIT 100;

SELECT
  DOM_FIRST_TEXT(DOM, '.brand') AS TITLE,
  DOM_FIRST_TEXT(DOM, '.pbox_price') AS PRICE,
  DOM_BASE_URI(DOM) AS URI,
  IN_BOX_FIRST_IMG(DOM, '405x405') AS MAIN_IMAGE,
  DOM_FIRST_TEXT(DOM, '#wrap_con') AS PARAMETERS_TEXT,
  DOM_CH(DOM) AS NCHAR,
  DOM_IMG(DOM) AS NIMG
FROM LOAD_OUT_PAGES(@LINK, @OUT_LINK_STRICT_CSS, 1, 100)
WHERE DOM_CH(DOM) > 100;;

-- Generated by TestManual
CALL SET_PAGE_EXPIRES('1s', 1);
SELECT DOM, DOM_TEXT(DOM) FROM LOAD_OUT_PAGES('https://www.mia.com/formulas.html', '*:expr(width > 240 && width < 250 && height > 360 && height < 370)', 0, 20);

-- Generated by TestManual
CALL DOM_LOAD('https://www.mia.com/formulas.html');

-- Generated by TestManual
SELECT DOM, TOP, LEFT, WIDTH, HEIGHT, IMG, A, SIBLING, DOM_TEXT(DOM), DOM_FIRST_HREF(DOM) FROM LOAD_AND_GET_FEATURES('https://www.mia.com/formulas.html') WHERE SIBLING>30 AND DOM_TEXT_LENGTH(DOM) > 10 AND TOP > 300 AND TOP < 3000;
SELECT DOM, TOP, LEFT, WIDTH, HEIGHT, IMG, A, CHAR, SIBLING, DOM_TEXT(DOM), DOM_FIRST_HREF(DOM) FROM LOAD_AND_GET_FEATURES('https://www.mia.com/item-1687128.html') WHERE SEQ > 170 AND SEQ < 400;
SELECT
  DOM_FIRST_TEXT(DOM, 'div:iN-bOx(560,27),*:IN-BOX(560,56)') AS TITLE,
  IN_BOX_FIRST_TEXT(DOM, '560x27,560x56') AS TITLE3
FROM LOAD_OUT_PAGES('https://www.mia.com/formulas.html', '*:expr(img>0 && width>200 && height>200 && sibling>30)', 1, 20)
WHERE DOM_CH(DOM) > 100;

-- Generated by TestManual
SELECT *
            FROM LOAD_AND_GET_FEATURES('https://www.mia.com/formulas.html')
            WHERE WIDTH BETWEEN 240 AND 250 AND HEIGHT BETWEEN 360 AND 370 LIMIT 10;
SELECT DOM_ABS_HREF(DOM_SELECT_FIRST(DOM, 'a')) AS HREF
            FROM LOAD_AND_GET_FEATURES('https://www.mia.com/formulas.html')
            WHERE WIDTH BETWEEN 240 AND 250 AND HEIGHT BETWEEN 360 AND 370 LIMIT 10;
SELECT DOM_ABS_HREF(DOM_SELECT_FIRST(DOM, 'a')) AS HREF
            FROM LOAD_AND_GET_FEATURES('https://www.mia.com/formulas.html')
            WHERE SIBLING > 250 LIMIT 10;

-- Generated by TestManual
SELECT * FROM LOAD_AND_GET_FEATURES('https://www.mia.com/formulas.html', '.nfList', 0, 20);
SELECT * FROM LOAD_AND_GET_FEATURES('https://www.mia.com/item-1687128.html', 'DIV,UL,UI,P', 0, 20);

-- Generated by TestManual
SELECT DOM, TOP, LEFT, WIDTH, HEIGHT, IMG, A, SIBLING, DOM_TEXT(DOM), DOM_FIRST_HREF(DOM) FROM LOAD_AND_GET_FEATURES('http://news.qq.com/world_index.shtml') WHERE SIBLING>20 AND DOM_TEXT_LENGTH(DOM) > 10 AND TOP > 300 AND TOP < 3000;
SELECT DOM, TOP, LEFT, WIDTH, HEIGHT, IMG, A, CHAR, SIBLING, DOM_TEXT(DOM), DOM_FIRST_HREF(DOM) FROM LOAD_AND_GET_FEATURES('http://new.qq.com/omn/20180424/20180424A104ZC.html') WHERE SEQ > 170 AND SEQ < 400;
SELECT
  DOM_FIRST_TEXT(DOM, 'H1') AS TITLE,
  DOM_FIRST_TEXT(DOM, '#LeftTool') AS DATE_TIME,
  DOM_FIRST_TEXT(DOM, '.content-article') AS CONTENT
FROM LOAD_OUT_PAGES('http://news.qq.com/world_index.shtml', '.Q-tpList', 1, 100);

-- Generated by TestManual
SELECT * FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '*:expr(width==248 && height==228)', 0, 5);
SELECT DOM_TITLE(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '*:expr(width==248 && height==228) a', 0, 5);

-- Generated by TestManual
SELECT
  IN_BOX_FIRST_TEXT(DOM, '560x27') AS TITLE,
  IN_BOX_FIRST_TEXT(DOM, '570x36') AS PRICE1,
  IN_BOX_FIRST_TEXT(DOM, '560x56') AS TITLE2,
  IN_BOX_FIRST_TEXT(DOM, '570x85') AS PRICE2,
  DOM_BASE_URI(DOM) AS URI,
  IN_BOX_FIRST_IMG(DOM, '405x405') AS MAIN_IMAGE,
  DOM_IMG(DOM) AS NIMG
FROM LOAD_OUT_PAGES('https://www.mia.com/formulas.html', '*:expr(img>0 && width>200 && height>200 && sibling>30)', 1, 10)
WHERE DOM_CH(DOM) > 100;;

-- Generated by TestManual
SELECT * FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.welcome');
SELECT * FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfPrice', 0, 5);
SELECT * FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfPic img', 0, 5);
SELECT * FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfPic a', 0, 5);
SELECT * FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), 'a[href~=item]', 0, 5);

-- Generated by TestManual
SELECT DOM_TITLE(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfPic a', 0, 5);
SELECT DOM_TITLE(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfPic a', 0, 5) WHERE LOCATE('白金版', DOM_TITLE(DOM)) > 0;
SELECT * FROM LOAD_AND_GET_FEATURES('https://www.mia.com/formulas.html') WHERE WIDTH=248 AND HEIGHT=228 LIMIT 100;

-- Generated by TestManual
SELECT * FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '*:in-box(*,*,323,31)');
SELECT * FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '*:in-box(*,*,229,36)', 0, 5);
SELECT IN_BOX_FIRST_TEXT(DOM_LOAD('https://www.mia.com/formulas.html'), '229x36');

-- Generated by TestManual
SELECT DOM_ABS_HREF(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '*:expr(width > 240 && width < 250 && height > 360 && height < 370) a', 0, 5);

-- Generated by TestManual
SELECT * FROM LOAD_AND_GET_LINKS('https://www.mia.com/formulas.html', '*:expr(width > 240 && width < 250 && height > 360 && height < 370)');

-- Generated by TestManual
SELECT DOM, DOM_TEXT(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfList', 0, 10);

-- Generated by TestManual
SELECT DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfList a', 0, 5);
SELECT DOM_ABS_HREF(DOM) FROM DOM_SELECT(DOM_LOAD('https://www.mia.com/formulas.html'), '.nfList a', 0, 5);

-- Generated by TestJavaObjectSerializer

-- Generated by TestJavaObjectSerializer

-- Generated by TestJavaObjectSerializer

-- Generated by TestExtractCases
SELECT * FROM LOAD_AND_GET_FEATURES('http://list.mogujie.com/book/jiadian/10059513 --expires=1s') WHERE SIBLING > 30;
SELECT
  DOM_BASE_URI(DOM) AS Uri,
  DOM_FIRST_TEXT(DOM, 'h1:expr(_char>10 && _img == 0)') AS Title,
  DOM_FIRST_TEXT(DOM, '.price') AS Price,
  DOM_FIRST_TEXT(DOM, '#J_ParameterTable') AS Parameters
FROM LOAD_OUT_PAGES_IGNORE_URL_QUERY('http://list.mogujie.com/book/jiadian/10059513', '*:expr(width>=210 && width<=230 && height>=380 && height<=420 && sibling>30 ) a[href~=detail]', 1, 1000);

-- Generated by TestExtractCases
SELECT * FROM LOAD_AND_GET_FEATURES('https://www.mia.com/formulas.html --expires=1s') LIMIT 20;

-- Generated by TestExtractCases
SELECT * FROM LOAD_AND_GET_LINKS('https://www.mia.com/formulas.html --expires=1s', 'div:expr(WIDTH>=210 && WIDTH<=230 && HEIGHT>=400 && HEIGHT<=420 && SIBLING>30 ) a[href~=detail]');

-- Generated by TestExtractCases
CALL SET_SCROLL_DOWN_COUNT(3, 1);
SELECT * FROM LOAD_AND_GET_FEATURES('https://list.jd.com/list.html?cat=670,671,672') WHERE SIBLING > 20;
SELECT
  DOM_FIRST_TEXT(DOM, '.sku-name') AS NAME,
  DOM_FIRST_TEXT(DOM, '.summary-price') AS PRICE,
  DOM_BASE_URI(DOM) AS URI,
  DOM_FIRST_IMG(DOM, '.main-img') AS MAIN_IMAGE,
  DOM_FIRST_IMG(DOM, '460x460') AS MAIN_IMAGE2,
  DOM_FIRST_TEXT(DOM, '.parameter2') AS PARAMETERS,
  DOM_FIRST_TEXT(DOM, '.comment-item') AS COMMENT1
FROM LOAD_OUT_PAGES('https://list.jd.com/list.html?cat=670,671,672', '*:expr(IMG>0 && WIDTH>200 && HEIGHT>200 && SIBLING>30)', 1, 20)
WHERE LOCATE('item', DOM_BASE_URI(DOM)) > 0;;

-- Generated by TestExtractCases
SELECT ADMIN_SAVE('https://www.mia.com/formulas.html', 'product.index.html');
SELECT ADMIN_SAVE('https://www.mia.com/item-1687128.html', 'product.detail.html');
SELECT ADMIN_SAVE('http://news.baidu.com/guoji', 'news.index.html');
SELECT ADMIN_SAVE('http://news.163.com/17/1119/09/D3JJF1290001875P.html', 'news.detail.html');

-- Generated by TestExtractCases
SELECT * FROM LOAD_AND_GET_FEATURES('https://www.mia.com/formulas.html --expires=1s') WHERE SIBLING > 30 LIMIT 20;
CALL SET_PAGE_EXPIRES('1d', 1);
SELECT
  DOM_BASE_URI(DOM) AS BaseUri,
  DOM_FIRST_TEXT(DOM, '.brand') AS Title,
  DOM_FIRST_TEXT(DOM, '.pbox_price') AS Price,
  DOMWIDTH(DOM_SELECT_FIRST(DOM, '.pbox_price')) AS WIDTH,
  DOMHEIGHT(DOM_SELECT_FIRST(DOM, '.pbox_price')) AS HEIGHT,
  DOM_FIRST_TEXT(DOM, '#wrap_con') AS Parameters
FROM LOAD_OUT_PAGES_IGNORE_URL_QUERY('https://www.mia.com/formulas.html', '*:expr(width>=250 && width<=260 && height>=360 && height<=370 && sibling>30 ) a', 1, 20);

-- Generated by TestExtractCases
CALL SET_SCROLL_DOWN_COUNT(3, 1);
SELECT * FROM LOAD_AND_GET_FEATURES('http://category.vip.com/search-1-0-1.html?q=3|29736') WHERE SIBLING > 20;
SELECT
  DOM_BASE_URI(DOM) AS Uri,
  DOM_FIRST_TEXT(DOM, '.pro-title-main') AS Title,
  DOM_FIRST_TEXT(DOM, '.price-sell') AS Price,
  DOM_FIRST_TEXT(DOM, '.g-pro-param') AS Parameters
FROM LOAD_OUT_PAGES_IGNORE_URL_QUERY('http://category.vip.com/search-1-0-1.html?q=3|29736', '.goods-list-item a[href~=detail]', 1, 1000);

-- Generated by TestPulsarH2
CREATE TABLE test(number INT, name VARCHAR);
INSERT INTO test VALUES (2, 'a'), (1, 'a'), (3, 'b'); INSERT INTO test VALUES (4, 'e'), (5, 'f'), (10, 'g');
CREATE INDEX ON test(name);
CREATE TABLE test2(number INT, name VARCHAR);
INSERT INTO test2 VALUES (12, 'ax'), (11, 'az'), (13, 'ay'); INSERT INTO test2 VALUES (14, 'bx'), (25, 'by'), (100, 'bz');
CREATE INDEX ON test2(name);
SELECT number, name AS n FROM test WHERE name LIKE '%a%' ORDER BY number;;
SELECT x/3 AS a, count(*) c FROM system_range(1, 10) GROUP BY a HAVING c>2;

-- Generated by TestPulsarH2
SELECT a,b FROM table(a INT=(1, 2, 3, 4), b CHAR=('x', 'y', 'w', 'z')) WHERE a>0 AND b IN ('x', 'y');

-- Generated by TestPulsarH2

-- Generated by TestPulsarH2
SELECT * FROM explode(array('A','B','C'));
SELECT * FROM explode(array());
SELECT * FROM explode();
SELECT * FROM posexplode(array('A','B','C'));

-- Generated by TestPulsarH2
SET @r=map('a', 1, 'b', 2, 'c', 3);
SELECT map('a', 1, 'b', 2, 'c', 3);

-- Generated by TestPulsarH2
SET @r=array(1, 2, 3);
SELECT @r;
SET @r=array('a', 'b', 1 + 3);
SELECT @r;

-- Generated by TestPulsarH2
CREATE TABLE test(number INT, name VARCHAR);
INSERT INTO test VALUES (2, 'a'), (1, 'a'), (3, 'b'); INSERT INTO test VALUES (4, 'e'), (5, 'f'), (10, 'g');
SET @r = (SELECT * FROM test LIMIT 1);
SELECT @r;

-- Generated by TestPulsarH2
CREATE TABLE test(id INT, name VARCHAR);
INSERT INTO test VALUES (2, 'a'), (1, 'a'), (3, 'b'); INSERT INTO test VALUES (4, 'e'), (5, 'f'), (10, 'g');
SELECT GROUP_CONCAT(name) FROM test;

-- Generated by TestPulsarH2
CREATE TABLE test(id INT, name VARCHAR);
INSERT INTO test VALUES (2, 'a'), (1, 'a'), (3, 'b'); INSERT INTO test VALUES (4, 'e'), (5, 'f'), (10, 'g');
SET @R=(SELECT GROUP_COLLECT(name) FROM test);
SELECT * FROM POSEXPLODE(@R);

-- Generated by TestPulsarH2
SELECT NULL||'A';

-- Generated by TestPulsarH2
create table `http://item.jd.com/19283721.html`(h1 varchar, `#jd-price` decimal);
CREATE INDEX ON `http://item.jd.com/19283721.html`(h1);
CREATE INDEX ON `http://item.jd.com/19283721.html`(`#jd-price`);
INSERT INTO `http://item.jd.com/19283721.html` VALUES('iphone', '6575'), ('lephone', '3998'), ('huawei phone', '2889'); INSERT INTO `http://item.jd.com/19283721.html`  VALUES('e', 200.1), ('f', 99.8), ('old huawei phone', 1000);
SELECT h1, `#jd-price` AS price FROM `http://item.jd.com/19283721.html` WHERE h1 LIKE '%phone%' AND `#jd-price` > 2000 ORDER BY `#jd-price` DESC;

-- Generated by TestPulsarH2

-- Generated by TestMetadataFunctions
SELECT * FROM META_PARSE('https://www.mia.com/item-1792382.html');