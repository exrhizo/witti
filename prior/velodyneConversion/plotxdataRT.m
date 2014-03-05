%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% This function captures M frames (revolutions), processes them and displays
% them, one frame at a time.
%
%   Created by Erick Ruiz
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [] = plotxdataRT(M)
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

%Capture, process and display 1 frame M times
for i=1:M
    clear data xdata
    dos('sudo ./savedump.exe','-echo'); %capture one revolution
    fid = fopen('datafile.bin','r');
    data = cast(fread(fid, '*uint8'),'double');
    fclose(fid);

    
    [xdata] = scatteredPoints (data); %process one revolution

    str=sprintf('Viewing frame %d/%d',i,M);    
    set(gcf,'Name',str);
    
    Y = xdata(:,1); %swap axis to correct image
    X = xdata(:,2); %swap axis to correct image
    Z = xdata(:,3);
    C = xdata(:,4);      
    set(p,'XData',X,'YData',Y,'ZData',Z,'CData',C); %
    drawnow;    %Display one revolution
end

