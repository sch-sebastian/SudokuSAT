clear all
close all
clc
format long
clc
d=dir('*.csv');
n=length(d);
tables = containers.Map;
for i=1:n
    tables(d(i).name) = table2array(readtable(d(i).name));
end

p = n/3;
encode_std = zeros(p,3);
solve_std = zeros(p,3);
encode_avg = zeros(p,3);
solve_avg = zeros(p,3);
names = strings([p,3]);

c1=1;
c2=1;
c3=1;
for i=1:n
    name = d(i).name;
    if contains(name,'_0_')
        t = tables(name);
        encode_std(c1,1) = std(t(:,1:1));
        solve_std(c1,1) = std(t(:,2:2));
        encode_avg(c1,1) = mean(t(:,1:1));
        solve_avg(c1,1) = mean(t(:,2:2));
        names(c1,1) = name;
        c1 = c1+1;
    elseif contains(name,'_1_')
        t = tables(name);
        encode_std(c2,2) = std(t(:,1:1));
        solve_std(c2,2) = std(t(:,2:2));
        encode_avg(c2,2) = mean(t(:,1:1));
        solve_avg(c2,2) = mean(t(:,2:2));
        names(c2,2) = name;
        c2 = c2+1;
    elseif contains(name,'_2_')
        t = tables(name);
        encode_std(c3,3) = std(t(:,1:1));
        solve_std(c3,3) = std(t(:,2:2));
        encode_avg(c3,3) = mean(t(:,1:1));
        solve_avg(c3,3) = mean(t(:,2:2));
        names(c3,3) = name;
        c3 = c3+1;
    end

end


f1 = figure;
ma = max([max(encode_avg(:,2)), max(encode_avg(:,1))]);
mi = min([min(encode_avg(:,2)), min(encode_avg(:,1))]);
%set(gca,'xscale','log')
%set(gca,'yscale','log')
set(gca,'fontname','times')
xlim([max([0,mi]),ma])
ylim([max([0,mi]),ma])
axis equal
hold on
scatter(encode_avg(:,2),encode_avg(:,1))
x= linspace(0,100);
y = 1*x;
plot(x,y)
hold off
%title('Encoding Time')
xlabel('PBCs + Combinations | t-encode | (ms)')
ylabel('PBCs | t-encode | (ms)') 
%legend('Moderate','Extra Deadly','Location','northwest')
print('-deps','opt_encode_2_1')

f2 = figure;
ma = max([max(encode_avg(:,3)), max(encode_avg(:,1))]);
mi = min([min(encode_avg(:,3)), min(encode_avg(:,1))]);
%set(gca,'xscale','log')
%set(gca,'yscale','log')
set(gca,'fontname','times')
xlim([max([0,mi]),ma])
ylim([max([0,mi]),ma])
axis equal
hold on
scatter(encode_avg(:,3),encode_avg(:,1))
x= linspace(0,100);
y = 1*x;
plot(x,y)
hold off
%title('Encoding Time')
xlabel('Combinations | t-encode | (ms)')
ylabel('PBCs | t-encode | (ms)') 
%legend('Moderate','Extra Deadly','Location','northwest')
print('-deps','opt_encode_3_1')

f3 = figure;
ma = max([max(solve_avg(:,2)), max(solve_avg(:,1))]);
mi = min([min(solve_avg(:,2)), min(solve_avg(:,1))]);
set(gca,'xscale','log')
set(gca,'yscale','log')
set(gca,'fontname','times')
xlim([max([eps,mi]),ma])
ylim([max([eps,mi]),ma])
axis equal
hold on
scatter(solve_avg(:,2),solve_avg(:,1))
x= linspace(eps,ma);
y = 1*x;
plot(x,y)
hold off
%title('Solving Time')
xlabel('PBCs + Combinations | t-solve | (ms)')
ylabel('PBCs | t-solve | (ms)') 
%legend('Moderate','Extra Deadly','Location','northwest')
print('-deps','opt_solve_2_1')

f4 = figure;
ma = max([max(solve_avg(:,3)), max(solve_avg(:,1))]);
mi = min([min(solve_avg(:,3)), min(solve_avg(:,1))]);
set(gca,'xscale','log')
set(gca,'yscale','log')
set(gca,'fontname','times')
xlim([max([eps,mi]),ma])
ylim([max([eps,mi]),ma])
axis equal
hold on
scatter(solve_avg(:,3),solve_avg(:,1))
x= linspace(eps,ma);
y = 1*x;
plot(x,y)
hold off
%title('Solving Time')
xlabel('Combinations | t-solve | (ms)')
ylabel('PBCs | t-solve | (ms)') 
%legend('Moderate','Extra Deadly','Location','northwest')
print('-deps','opt_solve_3_1')