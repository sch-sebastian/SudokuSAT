clear all
close all
format long
clc
d=dir('*.csv'); 
n=length(d);
BDD_tables = containers.Map;
AN_tables = containers.Map;
for i=1:n
    name = d(i).name;
    if contains(name,'BDD')
        BDD_tables(name) = table2array(readtable(d(i).name));
    else
        AN_tables(name) = table2array(readtable(d(i).name));
    end
end


level = containers.Map;
level("Sat4j_AdderNetwork_0_TheTimesK016.csv") = 1;
level("Sat4j_AdderNetwork_0_TheTimesK017.csv") = 1;
level("Sat4j_AdderNetwork_0_TheTimesK018.csv") = 1;
level("Sat4j_AdderNetwork_0_TheTimesK019.csv") = 1;
level("Sat4j_AdderNetwork_0_TheTimesK020.csv") = 1;
level("Sat4j_AdderNetwork_0_TheTimesK021.csv") = 1;
level("Sat4j_AdderNetwork_0_TheTimesK022.csv") = 1;
level("Sat4j_AdderNetwork_0_TheTimesK023.csv") = 1;
level("Sat4j_AdderNetwork_0_TheTimesK024.csv") = 1;
level("Sat4j_AdderNetwork_0_TheTimesK025.csv") = 1;

level("Sat4j_AdderNetwork_0_TheTimesUK191.csv") = 5;
level("Sat4j_AdderNetwork_0_TheTimesUK192.csv") = 5;
level("Sat4j_AdderNetwork_0_TheTimesUK193.csv") = 5;
level("Sat4j_AdderNetwork_0_TheTimesUK194.csv") = 5;
level("Sat4j_AdderNetwork_0_TheTimesUK195.csv") = 5;
level("Sat4j_AdderNetwork_0_TheTimesUK196.csv") = 5;
level("Sat4j_AdderNetwork_0_TheTimesUK197.csv") = 5;
level("Sat4j_AdderNetwork_0_TheTimesUK198.csv") = 5;
level("Sat4j_AdderNetwork_0_TheTimesUK199.csv") = 5;
level("Sat4j_AdderNetwork_0_TheTimesUK200.csv") = 5;

level("Sat4j_BDD_0_TheTimesK016.csv") = 1;
level("Sat4j_BDD_0_TheTimesK017.csv") = 1;
level("Sat4j_BDD_0_TheTimesK018.csv") = 1;
level("Sat4j_BDD_0_TheTimesK019.csv") = 1;
level("Sat4j_BDD_0_TheTimesK020.csv") = 1;
level("Sat4j_BDD_0_TheTimesK021.csv") = 1;
level("Sat4j_BDD_0_TheTimesK022.csv") = 1;
level("Sat4j_BDD_0_TheTimesK023.csv") = 1;
level("Sat4j_BDD_0_TheTimesK024.csv") = 1;
level("Sat4j_BDD_0_TheTimesK025.csv") = 1;

level("Sat4j_BDD_0_TheTimesUK191.csv") = 5;
level("Sat4j_BDD_0_TheTimesUK192.csv") = 5;
level("Sat4j_BDD_0_TheTimesUK193.csv") = 5;
level("Sat4j_BDD_0_TheTimesUK194.csv") = 5;
level("Sat4j_BDD_0_TheTimesUK195.csv") = 5;
level("Sat4j_BDD_0_TheTimesUK196.csv") = 5;
level("Sat4j_BDD_0_TheTimesUK197.csv") = 5;
level("Sat4j_BDD_0_TheTimesUK198.csv") = 5;
level("Sat4j_BDD_0_TheTimesUK199.csv") = 5;
level("Sat4j_BDD_0_TheTimesUK200.csv") = 5;


numLevels = 2;

numBDD = 10;
numAD = 10;

BDD_encode_avg = zeros(numBDD,5); 
BDD_solve_avg = zeros(numBDD,5);
BDD_clauses = zeros(numBDD,5);
BDD_names = strings([numBDD,5]);

AN_encode_avg = zeros(numAD,5); 
AN_solve_avg = zeros(numAD,5);
AN_clauses = zeros(numAD,5);
AN_names = strings([numAD,5]);


b1 = 1;
b5 = 1;

k = keys(BDD_tables) ;
for i=1:length(k)
    name = k{i};
    t = BDD_tables(name);
    if(level(name) == 1)
        BDD_names(b1,1) = name;
        BDD_encode_avg(b1,1) = mean(t(:,1:1));  
        BDD_solve_avg(b1,1) = mean(t(:,2:2));
        BDD_clauses(b1,1) = t(1:1,3:3);
        b1 = b1+1;
    elseif (level(name) == 5)
        BDD_names(b5,5) = name;
        BDD_encode_avg(b5,5) = mean(t(:,1:1));  
        BDD_solve_avg(b5,5) = mean(t(:,2:2));
        BDD_clauses(b5,5) = t(1:1,3:3);
        b5 = b5+1;
    end
end

a1 = 1;
a5 = 1;

k = keys(AN_tables) ;
for i=1:length(k)
    name = k{i};
    t = AN_tables(name);
    if(level(name) == 1)
        AN_names(a1,1) = name;
        AN_encode_avg(a1,1) = mean(t(:,1:1));  
        AN_solve_avg(a1,1) = mean(t(:,2:2));
        AN_clauses(a1,1) = t(1:1,3:3);
        a1 = a1+1;
    elseif (level(name) == 5)
        AN_names(a5,5) = name;
        AN_encode_avg(a5,5) = mean(t(:,1:1));  
        AN_solve_avg(a5,5) = mean(t(:,2:2));
        AN_clauses(a5,5) = t(1:1,3:3);
        a5 = a5+1;
    end
end



f1 = figure;
set(gca,'fontname','times')
xlim([72814,110630])
ylim([394,626])
hold on
scatter(BDD_clauses(:,1),BDD_encode_avg(:,1),'o')
scatter(BDD_clauses(:,5),BDD_encode_avg(:,5),'s')
hold off
%title({'Encoding Time per Clause','BDD'})
xlabel('clauses (#)') 
ylabel('t-encode (ms)')
legend('Moderate','Extra Deadly','Location','northwest')
print('-deps','killer_BDD_encode')

f2 = figure;
set(gca,'fontname','times')
xlim([20866,24126])
ylim([71,91])
hold on
scatter(AN_clauses(:,1),AN_encode_avg(:,1),'o')
scatter(AN_clauses(:,5),AN_encode_avg(:,5),'s')
hold off
%title({'Encoding Time per Clause','Adder Network'})
xlabel('clauses (#)') 
ylabel('t-encode (ms)')
legend('Moderate','Extra Deadly','Location','northwest')
print('-deps','killer_AN_encode')

f3 = figure;
set(gca,'fontname','times')
%set(gca,'xscale','log')
%set(gca,'yscale','log')
%xlim([72814,110630])
%ylim([36,11266])
%axis equal
hold on
scatter(BDD_clauses(:,1),BDD_solve_avg(:,1),'o')
scatter(BDD_clauses(:,5),BDD_solve_avg(:,5),'s')
hold off
%title({'Solving Time per Clause','BDD'})
xlabel('clauses (#)') 
ylabel('t-solve (ms)')
legend('Moderate','Extra Deadly','Location','northwest')
print('-deps','killer_BDD_solve')

f4 = figure;
set(gca,'fontname','times')
ma = max([max(AN_clauses(:,1)), max(AN_solve_avg(:,1)), max(AN_clauses(:,5)), max(AN_solve_avg(:,5))]);
mi = max([min(AN_clauses(:,1)), min(AN_solve_avg(:,1)), min(AN_clauses(:,5)), min(AN_solve_avg(:,5))]);
%set(gca,'xscale','log')
%set(gca,'yscale','log')
%xlim([0,43221])
%ylim([68,12903])
%axis equal
hold on
scatter(AN_clauses(:,1),AN_solve_avg(:,1),'o')
scatter(AN_clauses(:,5),AN_solve_avg(:,5),'s')
hold off
%title({'Solving Time per Clause','Adder Network'})
xlabel('clauses (#)') 
ylabel('t-solve (ms)')
legend('Moderate','Extra Deadly','Location','northwest')
print('-deps','killer_AN_solve')

f5 = figure;
set(gca,'fontname','times')
ma = max([max(AN_encode_avg(:,1)), max(BDD_encode_avg(:,1)), max(AN_encode_avg(:,5)), max(BDD_encode_avg(:,5)), ]);
mi = min([min(AN_encode_avg(:,1)), min(BDD_encode_avg(:,1)), min(AN_encode_avg(:,5)), min(BDD_encode_avg(:,5)), ]);
set(gca,'xscale','log')
set(gca,'yscale','log')
xlim([max([1,mi]),ma])
ylim([max([1,mi]),ma])
axis equal
hold on
scatter(AN_encode_avg(:,1),BDD_encode_avg(:,1),'o')
scatter(AN_encode_avg(:,5),BDD_encode_avg(:,5),'s')
x= linspace(eps,ma);
y = 1*x;
plot(x,y)
hold off
%title({'Encoding Time','BDD vs Adder Network'})
xlabel('Adder Network | t-encode | (ms)')
ylabel('BDD | t-encode | (ms)') 
legend('Moderate','Extra Deadly','Location','northeast')
print('-deps','killer_encode_compare')

f6 = figure;
set(gca,'fontname','times')
ma = max([max(AN_solve_avg(:,1)), max(BDD_solve_avg(:,1)), max(AN_solve_avg(:,5)), max(BDD_solve_avg(:,5)), ]);
mi = min([min(AN_solve_avg(:,1)), min(BDD_solve_avg(:,1)), min(AN_solve_avg(:,5)), min(BDD_solve_avg(:,5)), ]);
set(gca,'xscale','log')
set(gca,'yscale','log')
xlim([max([1,mi]),ma])
ylim([max([1,mi]),ma])
axis equal
hold on
scatter(AN_solve_avg(:,1),BDD_solve_avg(:,1),'o')
scatter(AN_solve_avg(:,5),BDD_solve_avg(:,5),'s')
x= linspace(eps,ma);
y = 1*x;
plot(x,y)
hold off
%title({'Solving Time','BDD vs Adder Network'})
xlabel('Adder Network | t-solve | (ms)')
ylabel('BDD | t-solve | (ms)') 
legend('Moderate','Extra Deadly','Location','northwest')
print('-deps','killer_solve_compare')
