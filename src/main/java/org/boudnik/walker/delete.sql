delete from dbo.Q1
from dbo.Q1
inner join dbo.P1 on dbo.Q1.Q1_c1=dbo.P1.P1_c1
inner join dbo.N1 on dbo.P1.P1_c1=dbo.N1.N1_c1 and dbo.P1.P1_c3=dbo.N1.N1_c3
inner join dbo.M on dbo.N1.N1_c1=dbo.M.M_c1
where M.id=2;

delete from dbo.Q2
from dbo.Q2
inner join dbo.P2 on dbo.Q2.Q2_c1=dbo.P2.id
inner join dbo.N1 on dbo.P2.P2_c2=dbo.N1.N1_c2
inner join dbo.M on dbo.N1.N1_c1=dbo.M.M_c1
where M.id=2;

delete from dbo.Q3
from dbo.Q3
inner join dbo.P2 on dbo.Q3.Q3_c2=dbo.P2.P2_c2 and dbo.Q3.Q3_c3=dbo.P2.P2_c3
inner join dbo.N1 on dbo.P2.P2_c2=dbo.N1.N1_c2
inner join dbo.M on dbo.N1.N1_c1=dbo.M.M_c1
where M.id=2;

delete from dbo.Q2
from dbo.Q2
inner join dbo.P2 on dbo.Q2.Q2_c1=dbo.P2.id
inner join dbo.N2 on dbo.P2.P2_c3=dbo.N2.N2_c3 and dbo.P2.P2_c4=dbo.N2.N2_c4
inner join dbo.M on dbo.N2.N2_c2=dbo.M.M_c2 and dbo.N2.N2_c3=dbo.M.M_c3
where M.id=2;

delete from dbo.Q3
from dbo.Q3
inner join dbo.P2 on dbo.Q3.Q3_c2=dbo.P2.P2_c2 and dbo.Q3.Q3_c3=dbo.P2.P2_c3
inner join dbo.N2 on dbo.P2.P2_c3=dbo.N2.N2_c3 and dbo.P2.P2_c4=dbo.N2.N2_c4
inner join dbo.M on dbo.N2.N2_c2=dbo.M.M_c2 and dbo.N2.N2_c3=dbo.M.M_c3
where M.id=2;

delete from dbo.P3
from dbo.P3
inner join dbo.N2 on dbo.P3.P3_c1=dbo.N2.id
inner join dbo.M on dbo.N2.N2_c2=dbo.M.M_c2 and dbo.N2.N2_c3=dbo.M.M_c3
where M.id=2;

delete from dbo.P1
from dbo.P1
inner join dbo.N1 on dbo.P1.P1_c1=dbo.N1.N1_c1 and dbo.P1.P1_c3=dbo.N1.N1_c3
inner join dbo.M on dbo.N1.N1_c1=dbo.M.M_c1
where M.id=2;

delete from dbo.Q3
from dbo.Q3
inner join dbo.N2 on dbo.Q3.Q3_c1=dbo.N2.id
inner join dbo.M on dbo.N2.N2_c2=dbo.M.M_c2 and dbo.N2.N2_c3=dbo.M.M_c3
where M.id=2;

delete from dbo.P2
from dbo.P2
inner join dbo.N2 on dbo.P2.P2_c3=dbo.N2.N2_c3 and dbo.P2.P2_c4=dbo.N2.N2_c4
inner join dbo.M on dbo.N2.N2_c2=dbo.M.M_c2 and dbo.N2.N2_c3=dbo.M.M_c3
where M.id=2;

delete from dbo.P2
from dbo.P2
inner join dbo.N1 on dbo.P2.P2_c2=dbo.N1.N1_c2
inner join dbo.M on dbo.N1.N1_c1=dbo.M.M_c1
where M.id=2;

delete from dbo.N1
from dbo.N1
inner join dbo.M on dbo.N1.N1_c1=dbo.M.M_c1
where M.id=2;

delete from dbo.N2
from dbo.N2
inner join dbo.M on dbo.N2.N2_c2=dbo.M.M_c2 and dbo.N2.N2_c3=dbo.M.M_c3
where M.id=2;

delete from dbo.M
where M.id=2;