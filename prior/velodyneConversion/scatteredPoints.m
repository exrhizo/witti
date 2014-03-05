function [xdata] = scatteredPoints (data)

load indexTables

if mod(length(data),400)~=0
    a=floor(length(data)/400);
    data=data(1:400*a);
end
N=length(data);
data=reshape(data,400,N/400)';


Px=zeros(N/400,32*4);
Py=zeros(N/400,32*4);
Pz=zeros(N/400,32*4);


correctionValues=[cval(33:64,:)',cval(1:32,:)',cval(1:32,:)',cval(1:32,:)']; %#ok<NODEF>

rot=[bitor(bitshift(data(:,4), 8),data(:,3))/100,...
     bitor(bitshift(data(:,104), 8),data(:,103))/100,...
     bitor(bitshift(data(:,204), 8),data(:,203))/100,...
     bitor(bitshift(data(:,304), 8),data(:,303))/100];

ct = data(:,Ix);
dt = bitor(bitshift(data(:,dmsb), 8),data(:,dlsb));


for i=1:128 
    Dcorr=correctionValues(3,i);%Distance correction factor
    Vo=correctionValues(4,i);%Vertical offset
    Ho=correctionValues(5,i);%Horizontal offset
    theta=correctionValues(2,i)*pi/180;%vertical correction angle
    alpha=correctionValues(1,i)*pi/180;%Rotational correction angle
    phi=rot(:,Ir(i))*pi/180;%rotational angle    
    dt(:,i)=0.2.*dt(:,i)+Dcorr; %absolute distance
    
    Px(:,i)=dt(:,i).*cos(theta).*(sin(phi).*cos(alpha)-cos(phi).*sin(alpha))-Ho.*(cos(phi).*cos(alpha)+sin(phi).*sin(alpha));
    Py(:,i)=dt(:,i).*cos(theta).*(cos(phi).*cos(alpha)+sin(phi).*sin(alpha))-Ho.*(sin(phi).*cos(alpha)-cos(phi).*sin(alpha));
    Pz(:,i)=dt(:,i).*sin(theta)+Vo;
end

a=size(Px);
N=a(1)*a(2);
xdata=[reshape(Px',N,1),reshape(Py',N,1),reshape(Pz',N,1),reshape(ct',N,1)];
xdata(:,1:3)=xdata(:,1:3)/100;

% a=size(rot);
% N=a(1)*a(2);
% rot=reshape(rot',N,1);






