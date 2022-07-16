clear all
format long
d=dir('*.csv'); 
n=length(d);
tables = containers.Map;
for i=1:n
    tables(d(i).name) = table2array(readtable(d(i).name));
end

encode_std = zeros(n,1);
solve_std = zeros(n,1);
encode_avg = zeros(n,1);
solve_avg = zeros(n,1);
clauses = zeros(n,1);
names = strings([n,1]);
for i=1:n
    name = d(i).name;
    t = tables(name);
    encode_std(i,1) = round(std(t(:,1:1)),2);  
    solve_std(i,1) = round(std(t(:,2:2)),2);
    encode_avg(i,1) = round(mean(t(:,1:1)),2);  
    solve_avg(i,1) = round(mean(t(:,2:2)),2) ;
    clauses(i,1) = t(1,3);
    names(i,1) = name;
end

res = table(names,encode_avg, solve_avg, encode_std, solve_std,clauses);
disp(res)