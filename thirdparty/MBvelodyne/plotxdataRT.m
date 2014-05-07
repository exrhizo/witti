%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% This function captures M frames (revolutions), processes them and displays
% them, one frame at a time.
%
%   Created by Erick Ruiz
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [data ] = plotxdataRT(M, fileReadsPerPlot)
% Setup the figure.
clf reset
set(gcf,'NextPlot','replace','Color','black','Renderer','opengl','Position',[10,50,1900,950])
colormap(jet) %color

% Setup the axes.
axis vis3d ij off
scale = 1.5;
set(gca,'XLim',[-25 20]*scale,'YLim',[-25 20]*scale,'ZLim',[-15 15]*scale)
view(56,26);camzoom(4);

% Create the patch.
p = patch( ...
    'Marker','.','MarkerSize',.05, ...
    'EdgeColor','none','FaceColor','none',...
    'MarkerEdgeColor','flat');

numberOfReads = fileReadsPerPlot;	% reading the file this many times is NOT efficient, but it shows that the C++ math works
X = zeros(3*128 * numberOfReads, 1);
Y = zeros(3*128 * numberOfReads, 1);
Z = zeros(3*128 * numberOfReads, 1);
C = zeros(3*128 * numberOfReads, 1);
%Capture, process and display 1 frame M times
for i=1:M
	for j=1:numberOfReads
		%clear data xdata
		%dos('sudo ./sa','-echo'); %capture one revolution
		dirInfo = dir('build/cartesianfile.dat');
		%index = strcmp({dirInfo.name},fileName);
	    fid = fopen('build/cartesianfile.dat','r');
		numberOfmeasurement = dirInfo.bytes/2048;
		
		xdata = fread(fid, [numberOfmeasurement*128 4],'float');
		%size(data)
		fclose(fid);
		if(size(xdata,2) >= 4)

			%[xdata] = scatteredPoints (data); %process one revolution
			%xdata = data;%cast(data, 'double');
	
			str=sprintf('Viewing frame %d/%d',i,M);    
			set(gcf,'Name',str);
    
		
			Y((j-1)*(numberOfmeasurement*128) + 1:j*(numberOfmeasurement*128)) = xdata(:,1); %swap axis to correct image
			X((j-1)*(numberOfmeasurement*128) + 1:j*(numberOfmeasurement*128)) = xdata(:,2); %swap axis to correct image
			Z((j-1)*(numberOfmeasurement*128) + 1:j*(numberOfmeasurement*128)) = xdata(:,3);
			C((j-1)*(numberOfmeasurement*128) + 1:j*(numberOfmeasurement*128)) = xdata(:,4);      
		end
	end
    set(p,'XData',X,'YData',Y,'ZData',Z,'CData',C); %
    drawnow;    %Display one revolution
	
end

