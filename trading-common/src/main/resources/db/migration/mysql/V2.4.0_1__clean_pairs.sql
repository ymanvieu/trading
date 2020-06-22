-- darty
delete from pair where source='DRTY';
-- fnac
update symbols set name='Fnac Darty SA' where code='FNAC';
update pair set name='Fnac Darty SA' where source='FNAC';

-- gameloft
delete from pair where source='GFT';

-- konami
delete from pair where source='KNM';