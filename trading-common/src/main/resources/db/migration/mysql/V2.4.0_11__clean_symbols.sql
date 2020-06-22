delete from rates where value = 0;
--
delete from pair where source = 'SFR';
delete from latestrates where fromcur = 'SFR';
delete from rates where fromcur = 'SFR';
delete from symbols where code = 'SFR';
--
delete from pair where source = 'BTG';
delete from latestrates where fromcur = 'BTG';
delete from rates where fromcur = 'BTG';
delete from symbols where code = 'BTG';