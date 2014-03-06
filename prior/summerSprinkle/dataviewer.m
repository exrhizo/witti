%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Thsi script reads the parsed modified binary file 
% created with savedump.exe, computes x,y,z points and
% plots the result, one frame at a time
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function result = dataviewer( filename, angleStr )

% clc;clear all
realTime = 0;

if( nargin < 1 || strcmp(filename,'-realtime') )
%     filename = '../data01.bin';
    disp 'We are going in real time';
    filename='datafile.bin';
    realTime=1;
end

if( ~ (nargin > 1) )
   % this should be the angle
   angleStr = '';
end

filename

% fid = fopen('data01.bin','r');
% fid = fopen('datafile.bin','r');

    % length to read in packets
    if( realTime > 0 )
        disp 'running converter to .bin file...';
        str = sprintf('sudo ./pcap2bin.exe -realtime %s', angleStr );
        dos( str );
    end
    
    fid = fopen(filename,'r');
    packetsToRead = 10000;
    while ~feof(fid) || realTime > 0
        data=fread(fid, 1200*packetsToRead, '*uint8');
        disp 'Read file...';
        data = cast(data,'double');

%         nPoints = length(data);
%         interval = (1200*360)*1000;   % 104544 is one "Frame" I think, but we need
                                % to gobble lots of these up at a time
        %                         interval = 2000;
%         interval = min( interval, length(data) );
        result.xdata = [];
        result.x = [];
        result.y = [];
        result.N = 0;

%         for i = 1:interval:length(data)

        [xdata,rot,dt,ct] = scatteredPoints (data);
        ct2=ct;
        a=size(ct2);
        N=a(1)*a(2);
        xdata(:,4)=reshape(ct2',N,1);


        y=find(diff(rot)<0);
        if ~isempty(y)
            x=y+1;
            x=x(1:end-1);
            y=y(2:end);
        end
        x=x.*32;y=y.*32;

        N=length(x);
        [x y];
        [result.x result.y];
        result.xdata = [result.xdata; xdata];
        result.x = x;
        result.y = y;
        result.N = N;

        % pause 0.1

        % TODO: save to a movie file



%         end
        if( realTime > 0 )
            plotxdata(result.xdata,result.x, result.y,1);
        else        
            plotxdata(result.xdata,result.x,result.y,result.N)
        end
        
        if( realTime > 0 )
            fclose(fid);
%             fid = fopen(fid);
            % rerun, and reopen the file
            if( realTime > 0 )
                disp 'running converter to .bin file...';
                dos( str );
            end
            
            fid = fopen(filename,'r');

        end
    
    end
    fclose(fid);

end