function [xdata,rot,dt,ct] = scatteredPoints (data)

load indexTables
startTime = cputime;

mTime = cputime;

if mod(length(data),100)~=0
%     disp 'mod...';
    a=floor(length(data)/400);
    data=data(1:400*a);
end

N=length(data);
data=reshape(data,400,N/400)';
% fprintf('Reshaping %d\n', cputime-mTime);

mTime = cputime;
Px=zeros(N/400,32*4);
Py=zeros(N/400,32*4);
Pz=zeros(N/400,32*4);
% fprintf('Reshaping %d\n', cputime-mTime);


correctionValues=[cval(33:64,:)',cval(1:32,:)',cval(1:32,:)',cval(1:32,:)']; %#ok<NODEF>
% correctionValues=[cval(1:32,:)',cval(1:32,:)',cval(1:32,:)',cval(1:32,:)']; %#ok<NODEF>
% correctionValues=[cval(1:32,:)',cval(1:32,:)',cval(1:32,:)',cval(33:64,:)']; %#ok<NODEF>
% correctionValues=[cval(33:64,:)',cval(33:64,:)',cval(33:64,:)',cval(1:32,:)']; %#ok<NODEF>

mTime = cputime;
rot=[(data(:,4)*256+data(:,3))/100,...
     (data(:,104)*256+data(:,103))/100,...
     (data(:,204)*256+data(:,203))/100,...
     (data(:,304)*256+data(:,303))/100];
% rot=[bitor(bitshift(data(:,4), 8),data(:,3))/100,...
%      bitor(bitshift(data(:,104), 8),data(:,103))/100,...
%      bitor(bitshift(data(:,204), 8),data(:,203))/100,...
%      bitor(bitshift(data(:,304), 8),data(:,303))/100];
% fprintf('Rotation %d\n', cputime-mTime);

mTime = cputime;
ct = data(:,Ix);
dt = (data(:,dmsb)*256+data(:,dlsb));
% dt = bitor(bitshift(data(:,dmsb), 8),data(:,dlsb));
% fprintf('dt %d\n', cputime-mTime);

% Eventually this will work, but there is a problem right now to try to use
% parfor on mac osX in matlab r13.
% http://www.mathworks.com/matlabcentral/answers/62496-java-mac-osx-10-6-update-12-problem-with-matlabpool
% matlabpool 4;
% 
% parfor i=1:128 
loopStartTime = cputime;
for i=1:128 
% for i=1:128 
    Dcorr=correctionValues(3,i);%Distance correction factor
    Vo=correctionValues(4,i);%Vertical offset
    Ho=correctionValues(5,i);%Horizontal offset
    theta=correctionValues(2,i)*pi/180;%vertical correction angle
    alpha=correctionValues(1,i)*pi/180;%Rotational correction angle
    phi=rot(:,Ir(i))*pi/180;%rotational angle    
    dt(:,i)=0.2.*dt(:,i)+Dcorr; %absolute distance
    
    if( 1 )
    % we can save time by not doing trig computations over and over
    cA = cos(alpha);
    sA = sin(alpha);
    cP = cos(phi);
    sP = sin(phi);
    cT = cos(theta);
    sT = sin(theta);
    
    Px(:,i)=dt(:,i).*cT.*(sP.*cA-cP.*sA)-Ho.*(cP.*cA+sP.*sA);
    Py(:,i)=dt(:,i).*cT.*(cP.*cA+sP.*sA)-Ho.*(sP.*cA-cP.*sA);
    Pz(:,i)=dt(:,i).*sT+Vo;

    else
    % clearly this way worked, but making multiple calls to trig functions 
    % is computationally expensive        
    Px(:,i)=dt(:,i).*cos(theta).*(sin(phi).*cos(alpha)-cos(phi).*sin(alpha))-Ho.*(cos(phi).*cos(alpha)+sin(phi).*sin(alpha));
    Py(:,i)=dt(:,i).*cos(theta).*(cos(phi).*cos(alpha)+sin(phi).*sin(alpha))-Ho.*(sin(phi).*cos(alpha)-cos(phi).*sin(alpha));
    Pz(:,i)=dt(:,i).*sin(theta)+Vo;
    
    end
end
loopEndTime = cputime;
diff = loopEndTime - loopStartTime;
% fprintf('Loop execution time %d seconds\n', diff);

a=size(Px);
N=a(1)*a(2);
afterLoop = cputime;

% it is my professional opinion that we can avoid swapping my breaking this
% down into iterations.

Px1 = reshape(Px',N,1);
% fprintf('Reshaped x %d\n', cputime-afterLoop);
Py1 = reshape(Py',N,1);
% fprintf('Reshaped y %d\n', cputime-afterLoop);
Pz1 = reshape(Pz',N,1);
% fprintf('Reshaped z %d\n', cputime-afterLoop);
ct1 = reshape(ct',N,1);
% fprintf('Reshaped ct %d\n', cputime-afterLoop);
xdata=[Px1,Py1,Pz1,ct1];
% fprintf('Reshaped x %d\n', cputime-afterLoop);
xdata(:,1:3)=xdata(:,1:3)/100;

afterLoop = cputime;
a=size(rot);
N=a(1)*a(2);
rot=reshape(rot',N,1);
% fprintf('Reshaped rot %d\n', cputime-afterLoop);

% fprintf('Total execution time %d\n', cputime-startTime);



end

% we can do this, because we know 
function result = iterateReshape( input, N, M )

    result = reshape(input, N, M );

end


