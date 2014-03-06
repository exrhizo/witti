% Script to create index tables and correction values
% for packet data processing (point cloud calculation).
%

load laserCorrection.mat   %load correction values table
correctionValues=[cval(33:64,:)',cval(1:32,:)',cval(1:32,:)',cval(1:32,:)']; %change to fit matrix data

hdrtest = [[221,238,238,238]; ...
           [238,221,238,238]; ...
           [238,238,221,238]; ...
           [238,238,238,221]];
Ihdr=[2,102,202,302];    %index to block header i.e. tmp=data2(1,Ihdr);

dmsb=zeros(1,96);  %index to MSB of distance 2 byte locations in nx400 matrix
dlsb=zeros(1,96);  %index to LSB of distance 2 byte locations in nx400 matrix
Ix=zeros(1,96);    %index to Intensity byte locations in nx400 matrix
for i=0:3
    for j=0:31
        dmsb(1,i*32+1+j) = i*100 + j*3+6;
        dlsb(1,i*32+1+j) = i*100 + j*3+5;
        Ix(1,i*32+1+j) = i*100 + j*3+7;
    end
end

Ir=[zeros(1,32)+1,zeros(1,32)+2,zeros(1,32)+3,zeros(1,32)+4];



save indexTables cval hdrtest Ihdr dmsb dlsb Ix Ir































